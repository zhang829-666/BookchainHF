<template>
    <div 
      :class="[
        'book-card',
        { 'list-mode': mode === 'list' },
        { 'grid-mode': mode === 'grid' },
        { 'with-actions': showActions }
      ]"
      @click="handleClick"
    >
      <!-- 封面 -->
      <div class="cover">
        < img 
          :src="book.coverUrl || placeholder" 
          alt="书籍封面" 
          :class="{ 'blind-cover': book.isBlindBox }"
        />
        <!-- 盲盒标记 -->
        <div v-if="book.isBlindBox" class="blind-box-badge">盲盒</div>
      </div>
  
      <!-- 内容区域 -->
      <div class="content">
        <h3 class="title">{{ book.title }}</h3>
        <div class="meta">
          <div v-if="!book.isBlindBox">
            <span>作者：{{ book.author || '未知' }}</span>
            <span>类别：{{ book.category || '未分类' }}</span>
          </div>
          <div v-if="book.isBlindBox">
            <span>神秘类别</span>
          </div>
        </div>
  
        <!-- 状态与价格 -->
        <div class="status">
          <span v-if="book.owner">
            所有者：{{ book.owner.username }}
          </span>
          <span v-if="book.price">￥{{ book.price }}</span>
        </div>
  
        <!-- 操作按钮 -->
        <div v-if="showActions" class="actions">
          <button 
            v-if="action === 'buy'" 
            class="buy-btn" 
            @click="handleBuy"
          >
            购买
          </button>
          <button 
            v-if="action === 'transfer'" 
            class="transfer-btn" 
            @click="handleTransfer"
          >
            转移
          </button>
          <button 
            v-if="action === 'view'" 
            class="view-btn" 
            @click="handleView"
          >
            查看详情
          </button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps, emit } from 'vue';
  
  // 接收props
  const props = defineProps({
    book: {
      type: Object,
      required: true,
      default: () => ({})
    },
    mode: {
      type: String,
      default: 'grid',
      values: ['grid', 'list']
    },
    showActions: {
      type: Boolean,
      default: false
    },
    action: {
      type: String,
      default: 'view',
      values: ['buy', 'transfer', 'view']
    },
    placeholder: {
      type: String,
      default: '/images/placeholder.png'
    }
  });
  
  // 触发事件
  const emit = defineEmits([
    'buy',
    'transfer',
    'view',
    'click'
  ]);
  
  // 点击卡片事件
  const handleClick = () => {
    emit('click', props.book);
  };
  
  // 购买操作
  const handleBuy = () => {
    emit('buy', props.book.bookId);
  };
  
  // 转移操作
  const handleTransfer = () => {
    emit('transfer', props.book.bookId);
  };
  
  // 查看详情操作
  const handleView = () => {
    emit('view', props.book.bookId);
  };
  </script>
  
  <style scoped>
  .book-card {
    display: flex;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    transition: transform 0.3s, box-shadow 0.3s;
    margin-bottom: 20px;
    background: white;
  }
  
  .list-mode {
    flex-direction: row;
    height: 150px;
  }
  
  .grid-mode {
    flex-direction: column;
    height: 300px;
    justify-content: space-between;
    align-items: center;
    text-align: center;
    padding: 15px;
  }
  
  .cover {
    position: relative;
    width: 100px;
    height: 150px;
    margin: 15px;
  }
  
  .grid-mode .cover {
    width: 150px;
    height: 220px;
    margin: 0;
  }
  
  .cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: opacity 0.3s;
  }
  
  .blind-cover {
    opacity: 0.7;
    background: #f5f5f5;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.9em;
    color: #666;
  }
  
  .blind-box-badge {
    position: absolute;
    top: 8px;
    right: 8px;
    background: #ff4444;
    color: white;
    font-size: 0.75em;
    padding: 2px 8px;
    border-radius: 12px;
  }
  
  .content {
    flex-grow: 1;
    padding: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  
  .title {
    font-size: 1.1em;
    color: #2c3e50;
    margin-bottom: 8px;
  }
  
  .meta {
    color: #7f8c8d;
    font-size: 0.95em;
    margin-bottom: 12px;
  }
  
  .status {
    font-size: 0.9em;
    color: #555;
  }
  
  .actions {
    display: flex;
    gap: 8px;
  }
  
  .buy-btn,
  .transfer-btn,
  .view-btn {
    padding: 6px 12px;
    border-radius: 4px;
    border: none;
    cursor: pointer;
    font-size: 0.9em;
    transition: background 0.3s;
  }
  
  .buy-btn {
    background: #2ecc71;
    color: white;
  }
  
  .transfer-btn {
    background: #3498db;
    color: white;
  }
  
  .view-btn {
    background: #9b59b6;
    color: white;
  }
  
  .book-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  }
  </style>