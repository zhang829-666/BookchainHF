<template>
    <div class="my-books-container">
      <h2 class="title">我的书籍</h2>
      <div class="filter-section">
        <label>筛选类型：</label>
        <select v-model="filterType">
          <option value="">全部</option>
          <option value="NORMAL">普通书籍</option>
          <option value="BLIND_BOX">盲盒</option>
        </select>
      </div>
  
      <div class="book-list">
        <div
          v-for="book in filteredBooks"
          :key="book.bookId"
          class="book-card"
        >
          <div class="book-info">
            < img :src="book.coverUrl || '/images/placeholder.png'" alt="书籍封面" />
            <div class="details">
              <h3>{{ book.title }}</h3>
              <p>作者：{{ book.author || '未知' }}</p >
              <p>类别：{{ book.category || '未分类' }}</p >
              <p>所有权：{{ book.ownerUsername }}</p >
            </div>
          </div>
  
          <div class="actions">
            <button v-if="isOwner(book)" class="transfer-btn" @click="handleTransfer(book)">
              转移所有权
            </button>
            <button class="tx-btn" @click="viewTransaction(book)">
              查看链上记录
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue';
  import { useUserStore } from '@/stores/user';
  import { getUserBooks, transferBookOwnership } from '@/api/book'; // 引入书籍接口
  import { useRouter } from 'vue-router';
  
  const userStore = useUserStore();
  const router = useRouter();
  const filterType = ref('');
  
  // 模拟用户书籍数据（实际需从后端获取）
  const userBooks = ref([
    {
      bookId: 'BOOK_001',
      title: 'Effective Java',
      author: 'Joshua Bloch',
      category: '技术',
      coverUrl: '/images/books/effective-java.jpg',
      ownerUserId: 123,
      blockchainTxHash: 'TX_001',
      type: 'NORMAL'
    },
    {
      bookId: 'BOX_001',
      title: '神秘盲盒',
      author: null,
      category: 'BLIND_BOX',
      coverUrl: '/images/blind-box-placeholder.png',
      ownerUserId: 123,
      blockchainTxHash: 'TX_002',
      type: 'BLIND_BOX'
    }
  ]);
  
  // 过滤书籍类型
  const filteredBooks = computed(() => {
    if (!filterType.value) return userBooks.value;
    return userBooks.value.filter(book => book.type === filterType.value);
  });
  
  // 判断是否为当前用户所有
  const isOwner = (book) => book.ownerUserId === userStore.userId;
  
  // 转移所有权
  const handleTransfer = (book) => {
    const newOwnerId = prompt('请输入新所有者的用户ID');
    if (newOwnerId) {
      transferBookOwnership(book.bookId, newOwnerId)
        .then(() => {
          book.ownerUserId = newOwnerId; // 更新本地状态
          alert('所有权转移成功');
        })
        .catch(error => {
          console.error('转移失败:', error);
          alert('转移失败，请检查用户ID');
        });
    }
  };
  
  // 查看区块链交易
  const viewTransaction = (book) => {
    if (book.blockchainTxHash) {
      router.push({
        name: 'TransactionDetail',
        params: { txHash: book.blockchainTxHash }
      });
    }
  };
  
  // 初始化获取用户书籍（实际在onMounted中调用）
  // onMounted(() => {
  //   getUserBooks().then(data => {
  //     userBooks.value = data;
  //   });
  // });
  </script>
  
  <style scoped>
  .my-books-container {
    padding: 30px;
  }
  
  .title {
    text-align: center;
    margin-bottom: 25px;
    color: #2c3e50;
  }
  
  .filter-section {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
  }
  
  .book-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 25px;
  }
  
  .book-card {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.08);
    transition: transform 0.3s;
  }
  
  .book-card:hover {
    transform: translateY(-5px);
  }
  
  .book-info {
    display: flex;
    align-items: center;
    gap: 20px;
    flex-grow: 1;
  }
  
  .book-info img {
    width: 80px;
    height: 120px;
    object-fit: cover;
    border-radius: 6px;
  }
  
  .details {
    line-height: 1.6;
    color: #555;
  }
  
  .actions {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .transfer-btn {
    padding: 8px 20px;
    background: #e74c3c;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .transfer-btn:hover {
    background: #c0392b;
  }
  
  .tx-btn {
    padding: 8px 20px;
    background: #3498db;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .tx-btn:hover {
    background: #2980b9;
  }
  </style>