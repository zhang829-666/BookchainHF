package org.bookchain;

import com.ibm.websphere.security.auth.callback.Callback;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 书籍区块链智能合约，实现资产创建、转移、查询等功能
 */
public class BookChaincode extends ChaincodeBase {

    // 资产类型：普通书籍
    private static final String ASSET_TYPE_NORMAL = "NORMAL";
    // 资产类型：盲盒书籍
    private static final String ASSET_TYPE_BLIND_BOX = "BLIND_BOX";
    // 资产状态字段
    private static final String FIELD_ASSET_ID = "assetId";
    private static final String FIELD_OWNER = "owner";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_TIMESTAMP = "timestamp";

    @Override
    public Response init(ChaincodeStub stub) {
        // 初始化表结构（示例：使用键值对存储资产）
        return newSuccessResponse("链码初始化成功");
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String function = stub.getFunction();
        Map<String, String> params = getParameters(stub);

        try {
            switch (function) {
                case "createAsset":
                    return createAsset(stub, params);
                case "transferAsset":
                    return transferAsset(stub, params);
                case "readAsset":
                    return readAsset(stub, params);
                case "queryAllAssets":
                    return queryAllAssets(stub, params);
                default:
                    return newErrorResponse("无效的函数调用");
            }
        } catch (Exception e) {
            return newErrorResponse("操作失败：" + e.getMessage());
        }
    }

    /**
     * 创建资产（书籍/盲盒）
     */
    private Response createAsset(ChaincodeStub stub, Map<String, String> params) {
        String assetId = params.get(FIELD_ASSET_ID);
        String owner = params.get(FIELD_OWNER);
        String type = params.getOrDefault(FIELD_TYPE, ASSET_TYPE_NORMAL);
        String timestamp = params.getOrDefault(FIELD_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        // 校验参数
        if (assetId == null || owner == null) {
            return newErrorResponse("资产ID和所有者地址为必填项");
        }
        if (!type.equals(ASSET_TYPE_NORMAL) && !type.equals(ASSET_TYPE_BLIND_BOX)) {
            return newErrorResponse("无效的资产类型（NORMAL/BLIND_BOX）");
        }

        // 构造资产数据
        JSONObject asset = new JSONObject();
        asset.put(FIELD_ASSET_ID, assetId);
        asset.put(FIELD_OWNER, owner);
        asset.put(FIELD_TYPE, type);
        asset.put(FIELD_TIMESTAMP, timestamp);

        // 写入区块链
        stub.putState(assetId, asset.toString().getBytes());
        return newSuccessResponse("资产创建成功");
    }

    /**
     * 转移资产所有权
     */
    private Response transferAsset(ChaincodeStub stub, Map<String, String> params) {
        String assetId = params.get(FIELD_ASSET_ID);
        String newOwner = params.get(FIELD_OWNER);

        // 校验参数
        if (assetId == null || newOwner == null) {
            return newErrorResponse("资产ID和新所有者地址为必填项");
        }

        // 查询当前资产状态
        byte[] assetBytes = stub.getState(assetId);
        if (assetBytes == null || assetBytes.length == 0) {
            return newErrorResponse("资产不存在");
        }

        // 更新所有者
        JSONObject asset = new JSONObject(new String(assetBytes));
        asset.put(FIELD_OWNER, newOwner);
        stub.putState(assetId, asset.toString().getBytes());

        return newSuccessResponse("所有权转移成功");
    }

    /**
     * 查询单个资产
     */
    private Response readAsset(ChaincodeStub stub, Map<String, String> params) {
        String assetId = params.get(FIELD_ASSET_ID);
        if (assetId == null) {
            return newErrorResponse("资产ID为必填项");
        }

        byte[] assetBytes = stub.getState(assetId);
        if (assetBytes == null || assetBytes.length == 0) {
            return newErrorResponse("资产不存在");
        }

        return newSuccessResponse(new String(assetBytes));
    }

    /**
     * 查询所有资产（可按类型过滤）
     */
    private Response queryAllAssets(ChaincodeStub stub, Map<String, String> params) {
        String typeFilter = params.get(FIELD_TYPE);
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        JSONArray assets = new JSONArray();
        try (QueryResultsIterator<KeyValue> iter = results) {
            for (KeyValue kv : iter) {
                JSONObject asset = new JSONObject(kv.getStringValue());
                if (typeFilter != null && !asset.getString(FIELD_TYPE).equals(typeFilter)) {
                    continue; // 按类型过滤
                }
                assets.put(asset);
            }
        }

        return newSuccessResponse(assets.toString());
    }

    /**
     * 解析调用参数
     */
    private Map<String, String> getParameters(ChaincodeStub stub) {
        String[] args = stub.getStringArgs();
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                params.put(args[i], args[i + 1]);
            }
        }
        return params;
    }

    public static void main(String[] args) {
        new BookChaincode().start(args);
    }
}
