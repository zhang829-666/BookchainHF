<template>
    <div class="transaction-list-container">
      <div class="filter-bar">
        <div class="filter-item">
          <label>交易类型：</label>
          <select v-model="filterType">
            <option value="">全部类型</option>
            <option value="TRANSFER">所有权转移</option>
            <option value="CREATE_BLIND_BOX">创建盲盒</option>
          </select>
        </div>
  
        <div class="filter-item">
          <label>时间范围：</label>
          <date-picker
            v-model="filterDateRange"
            type="daterange"
            placeholder="选择时间范围"
          />
        </div>
      </div>
  
      <table class="transaction-table">
        <thead>
          <tr>
            <th>交易哈希</th>
            <th>类型</th>
            <th>书籍</th>
            <th>参与者</th>
            <th>时间</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(tx, index) in filteredTransactions"
            :key="tx.txHash"
            :class="{ 'pending': tx.status === 'PENDING' }"
          >
            <td>{{ tx.txHash.slice(0, 8) + '...' }}</td>
            <td>{{ tx.type === 'TRANSFER' ? '转移' : '创建盲盒' }}</td>
            <td>{{ tx.bookTitle || '未知书籍' }}</td>
            <td>
              <div v-if="tx.type === 'TRANSFER'">
                转出：{{ tx.sender }} <br>
                转入：{{ tx.receiver }}
              </div>
              <div v-else>
                创建者：{{ tx.creator }}
              </div>
            </td>
            <td>{{ formatDate(tx.timestamp) }}</td>
            <td>
              <span :class="statusClass(tx.status)">
                {{ tx.status === 'COMPLETED' ? '已上链' : tx.status === 'PENDING' ? '处理中' : '失败' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
  
      <pagination
        v-if="total > 0"
        :total="total"
        :current-page="currentPage"
        @change="handlePageChange"
      />
    </div>
  </template>
  
  <script setup>
  import { defineProps, ref, computed } from 'vue';
  import DatePicker from '@/components/DatePicker.vue'; // 假设存在日期选择组件
  import { formatDate } from '@/utils/date'; // 日期格式化工具
  
  // 接收props
  const props = defineProps({
    transactions: {
      type: Array,
      required: true,
      default: () => []
    },
    total: {
      type: Number,
      default: 0
    }
  });
  
  // 状态管理
  const filterType = ref('');
  const filterDateRange = ref([]);
  const currentPage = ref(1);
  
  // 计算过滤后的数据
  const filteredTransactions = computed(() => {
    let result = props.transactions;
  
    // 按类型过滤
    if (filterType.value) {
      result = result.filter(tx => tx.type === filterType.value);
    }
  
    // 按时间范围过滤
    if (filterDateRange.value.length === 2) {
      const [start, end] = filterDateRange.value;
      result = result.filter(tx => {
        const txDate = new Date(tx.timestamp);
        return txDate >= start && txDate <= end;
      });
    }
  
    // 分页处理
    const startIndex = (currentPage.value - 1) * 10;
    const endIndex = startIndex + 10;
    return result.slice(startIndex, endIndex);
  });
  
  // 状态颜色映射
  const statusClass = (status) => {
    return {
      'text-success': status === 'COMPLETED',
      'text-warning': status === 'PENDING',
      'text-danger': status === 'FAILED'
    };
  };
  
  // 分页回调
  const handlePageChange = (page) => {
    currentPage.value = page;
    props.onPageChange(page); // 触发父组件分页事件
  };
  </script>
  
  <style scoped>
  .transaction-list-container {
    width: 100%;
    overflow-x: auto;
  }
  
  .filter-bar {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
    padding: 15px;
    background: #f8f9fa;
    border-radius: 8px;
  }
  
  .filter-item {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .transaction-table {
    width: 100%;
    min-width: 800px;
    border-collapse: collapse;
  }
  
  th, td {
    padding: 12px 15px;
    text-align: left;
    border-bottom: 1px solid #e0e0e0;
  }
  
  th {
    background: #f5f5f5;
    font-weight: 500;
    color: #333;
  }
  
  .pending {
    background: #fff8d8;
  }
  
  .statusClass {
    font-weight: 500;
    padding: 3px 8px;
    border-radius: 4px;
  }
  
  .text-success {
    color: #2ecc71;
    border: 1px solid #d0f3d8;
  }
  
  .text-warning {
    color: #f39c12;
    border: 1px solid #fff3cd;
  }
  
  .text-danger {
    color: #e74c3c;
    border: 1px solid #ffe6e6;
  }
  
  .pagination {
    margin-top: 25px;
    text-align: right;
  }
  </style>