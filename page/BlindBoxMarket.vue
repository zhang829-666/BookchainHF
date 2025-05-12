<template>
    <div class="blind-box-market">
      <h2 class="title">盲盒市场</h2>
      <div class="filter-section">
        <label>筛选类别：</label>
        <select v-model="filterCategory">
          <option value="">全部类别</option>
          <option value="文学">文学</option>
          <option value="科幻">科幻</option>
          <option value="技术">技术</option>
        </select>
      </div>
  
      <div class="grid-container">
        <div
          v-for="(box, index) in filteredBoxes"
          :key="box.bookId"
          class="blind-box-card"
          @click="openBox(box)"
        >
          <div class="front-face" :class="box.isOpen ? 'open' : ''">
            < img src="@/assets/blind-box-placeholder.png" alt="盲盒封面" />
            <div class="title">{{ box.title }}</div>
            <div class="price">￥{{ box.price }}</div>
          </div>
  
          <div class="back-face" v-show="box.isOpen">
            < img :src="box.coverUrl" alt="书籍封面" />
            <div class="details">
              <div class="title">{{ box.realTitle }}</div>
              <div class="author">作者：{{ box.author }}</div>
              <div class="category">类别：{{ box.category }}</div>
            </div>
            <button v-if="!box.isOwned" class="buy-btn" @click="purchaseBox(box)">立即购买</button>
            <button v-if="box.isOwned && !box.isOpen" class="open-btn" @click="revealBox(box)">打开盲盒</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, computed } from 'vue';
  import { useUserStore } from '@/stores/user';
  import { purchaseBlindBox, revealBlindBox } from '@/api/blindBox'; // 引入盲盒相关接口
  
  const userStore = useUserStore();
  const blindBoxes = ref([
    // 模拟盲盒数据（实际需从后端获取）
    {
      bookId: 'BOX_001',
      title: '神秘盲盒',
      price: 19.9,
      realTitle: '三体全集',
      author: '刘慈欣',
      category: '科幻',
      coverUrl: '/images/books/santi.jpg',
      isOpen: false,
      isOwned: false
    },
    {
      bookId: 'BOX_002',
      title: '知识盲盒',
      price: 29.9,
      realTitle: 'Java核心技术',
      author: 'Cay S. Horstmann',
      category: '技术',
      coverUrl: '/images/books/java-core.jpg',
      isOpen: false,
      isOwned: false
    }
  ]);
  
  const filterCategory = ref('');
  const filteredBoxes = computed(() => {
    if (!filterCategory.value) return blindBoxes.value;
    return blindBoxes.value.filter(box => box.category === filterCategory.value);
  });
  
  // 购买盲盒
  const purchaseBox = (box) => {
    if (!userStore.isLoggedIn) {
      alert('请先登录');
      return;
    }
    purchaseBlindBox(box.bookId).then(() => {
      box.isOwned = true;
      userStore.decreaseBalance(box.price); // 扣除用户余额
    });
  };
  
  // 打开盲盒（模拟区块链揭秘过程）
  const revealBox = (box) => {
    if (!box.isOwned) return;
    box.isOpen = true;
    
    // 模拟链上数据解密（实际调用后端解密接口）
    revealBlindBox(box.bookId).then(data => {
      box.realTitle = data.title;
      box.author = data.author;
      box.category = data.category;
      box.coverUrl = data.coverUrl;
    });
  };
  
  // 打开盲盒动画钩子（可扩展为全局动画库）
  const openBox = (box) => {
    if (box.isOpen || !box.isOwned) return;
    box.isOpen = true;
    setTimeout(() => { box.isOpen = false; }, 1500); // 短暂展示动画
  };
  </script>
  
  <style scoped>
  .blind-box-market {
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
  
  .grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 25px;
  }
  
  .blind-box-card {
    position: relative;
    perspective: 1000px;
    height: 350px;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    transition: transform 0.6s;
  }
  
  .front-face,
  .back-face {
    position: absolute;
    width: 100%;
    height: 100%;
    backface-visibility: hidden;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: #fff;
  }
  
  .back-face {
    transform: rotateY(180deg);
  }
  
  .blind-box-card.open .front-face {
    transform: rotateY(180deg);
  }
  
  .blind-box-card.open .back-face {
    transform: rotateY(0deg);
  }
  
  .front-face img {
    width: 150px;
    margin-bottom: 15px;
  }
  
  .title {
    font-size: 1.1em;
    font-weight: 500;
    color: #3498db;
    margin-bottom: 10px;
  }
  
  .price {
    color: #27ae60;
    font-size: 1.2em;
    font-weight: 600;
  }
  
  .back-face img {
    width: 120px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
  
  .details {
    text-align: center;
    margin-bottom: 20px;
    color: #555;
  }
  
  .buy-btn,
  .open-btn {
    padding: 10px 25px;
    background: #3498db;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .buy-btn:hover,
  .open-btn:hover {
    background: #2980b9;
  }
  
  .open-btn {
    background: #e74c3c;
  }
  </style>