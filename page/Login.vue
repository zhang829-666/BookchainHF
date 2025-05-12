<template>
    <div class="login-container">
      <h2 class="title">BookChain 登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名/邮箱</label>
          <input
            type="text"
            v-model.trim="form.username"
            :class="{ error: errors.username }"
            placeholder="请输入用户名或邮箱"
          />
          <p v-if="errors.username" class="error-message">{{ errors.username }}</p >
        </div>
  
        <div class="form-group">
          <label>密码</label>
          <input
            type="password"
            v-model.trim="form.password"
            :class="{ error: errors.password }"
            placeholder="请输入密码"
          />
          <p v-if="errors.password" class="error-message">{{ errors.password }}</p >
        </div>
  
        <button type="submit" class="login-btn" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '立即登录' }}
        </button>
  
        <div class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { login } from '@/api/user'; // 引入登录接口
  import { setToken } from '@/utils/auth'; // 引入Token存储工具
  
  const router = useRouter();
  const form = reactive({
    username: '',
    password: ''
  });
  
  const errors = reactive({
    username: '',
    password: ''
  });
  
  const isLoading = ref(false);
  
  // 表单验证
  const validateForm = () => {
    let isValid = true;
    errors.username = '';
    errors.password = '';
  
    if (!form.username) {
      errors.username = '请输入用户名或邮箱';
      isValid = false;
    }
  
    if (!form.password) {
      errors.password = '请输入密码';
      isValid = false;
    }
  
    return isValid;
  };
  
  const handleLogin = async () => {
    if (!validateForm()) return;
  
    try {
      isLoading.value = true;
      const response = await login(form);
      // 假设接口返回包含token的响应
      setToken(response.data.token);
      router.push('/dashboard'); // 登录成功后跳转
    } catch (error) {
      console.error('登录失败:', error.response?.data?.message || '未知错误');
      if (error.response?.status === 401) {
        errors.password = '用户名或密码错误';
      }
    } finally {
      isLoading.value = false;
    }
  };
  
  // 自动聚焦用户名输入框
  watch(() => router.currentRoute.value.fullPath, () => {
    const input = document.querySelector('input[type="text"]');
    input?.focus();
  });
  </script>
  
  <style scoped>
  .login-container {
    max-width: 400px;
    margin: 50px auto;
    padding: 30px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 0 15px rgba(0,0,0,0.1);
  }
  
  .title {
    text-align: center;
    margin-bottom: 30px;
    color: #2c3e50;
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  label {
    display: block;
    margin-bottom: 8px;
    color: #333;
    font-weight: 500;
  }
  
  input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
    transition: border-color 0.3s;
  }
  
  input.error {
    border-color: #e74c3c;
  }
  
  .error-message {
    color: #e74c3c;
    font-size: 0.9em;
    margin-top: 5px;
  }
  
  .login-btn {
    width: 100%;
    padding: 14px;
    background: #3498db;
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .login-btn:disabled {
    background: #bdc3c7;
    cursor: not-allowed;
  }
  
  .register-link {
    text-align: center;
    margin-top: 20px;
    color: #7f8c8d;
  }
  
  .register-link a {
    color: #3498db;
    text-decoration: none;
    font-weight: 500;
  }
  </style>