<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

const navItems = [
    {
        group: '内容',
        items: [
            { label: '文章列表', icon: 'ti-article', to: '/admin/articles' },
            { label: '发布文章', icon: 'ti-pencil', to: '/admin/articles/create' },
            { label: '评论管理', icon: 'ti-message', to: '/admin/comments' },
            { label: '分类管理', icon: 'ti-folder', to: '/admin/categories' },
            { label: '标签管理', icon: 'ti-tag', to: '/admin/tags' },
        ]
    },
    {
        group: '系统',
        items: [
            { label: '黑名单', icon: 'ti-ban', to: '/admin/blacklist' },
            { label: '个人资料', icon: 'ti-user', to: '/admin/profile' },
            { label: '站点设置', icon: 'ti-settings', to: '/admin/settings' },
        ]
    }
]

const isActive = (to) => route.path === to || route.path.startsWith(to + '/')
</script>

<template>
    <div class="admin-layout">
        <aside class="sidebar">
            <div class="sidebar-logo">
                <span class="logo-text">✦ 后台管理</span>
            </div>
            <nav class="sidebar-nav">
                <template v-for="group in navItems" :key="group.group">
                    <div class="nav-group-label">{{ group.group }}</div>
                    <router-link
                        v-for="item in group.items"
                        :key="item.to"
                        :to="item.to"
                        :class="['nav-item', { active: isActive(item.to) }]"
                    >
                        <i :class="['ti', item.icon]" aria-hidden="true"></i>
                        {{ item.label }}
                    </router-link>
                </template>
            </nav>
            <div class="sidebar-footer">
                <router-link to="/home" class="back-link">
                    <i class="ti ti-arrow-left" aria-hidden="true"></i>返回前台
                </router-link>
            </div>
        </aside>

        <div class="main">
            <header class="topbar">
                <div class="breadcrumb">
                    <span class="bc-root">后台管理</span>
                    <span class="bc-sep">/</span>
                    <span class="bc-current">{{ $route.meta.title }}</span>
                </div>
            </header>
            <div class="content">
                <router-view />
            </div>
        </div>
    </div>
</template>

<style scoped>
.admin-layout { display:flex; height:100vh; overflow:hidden; background:var(--surface-0,#f5f4fd); }
.sidebar { width:168px; flex-shrink:0; background:#1a1a2e; display:flex; flex-direction:column; }
.sidebar-logo { padding:1rem; border-bottom:0.5px solid rgba(255,255,255,.08); }
.logo-text { font-size:14px; font-weight:500; color:#AFA9EC; letter-spacing:.02em; }
.sidebar-nav { flex:1; overflow-y:auto; padding:0.5rem 0; }
.nav-group-label { padding:8px 12px 4px; font-size:10px; color:rgba(255,255,255,.3); text-transform:uppercase; letter-spacing:.08em; margin-top:8px; }
.nav-item { display:flex; align-items:center; gap:8px; padding:8px 12px; font-size:12px; color:rgba(255,255,255,.5); text-decoration:none; transition:all .15s; border-left:2px solid transparent; }
.nav-item:hover { color:rgba(255,255,255,.85); background:rgba(255,255,255,.04); }
.nav-item.active { color:#AFA9EC; background:rgba(175,169,236,.12); border-left-color:#AFA9EC; }
.nav-item .ti { font-size:15px; }
.sidebar-footer { padding:10px 12px; border-top:0.5px solid rgba(255,255,255,.08); }
.back-link { display:flex; align-items:center; gap:6px; font-size:11px; color:rgba(255,255,255,.35); text-decoration:none; transition:color .15s; }
.back-link:hover { color:rgba(255,255,255,.7); }
.main { flex:1; display:flex; flex-direction:column; overflow:hidden; }
.topbar { height:44px; background:var(--surface-2,#fff); border-bottom:0.5px solid var(--border,#eee); display:flex; align-items:center; padding:0 1.2rem; flex-shrink:0; }
.breadcrumb { display:flex; align-items:center; gap:6px; font-size:12px; }
.bc-root, .bc-sep { color:var(--text-muted,#aaa); }
.bc-current { color:var(--text-primary,#222); font-weight:500; }
.content { flex:1; overflow-y:auto; padding:1.2rem; }
</style>
