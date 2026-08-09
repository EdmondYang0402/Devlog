<script setup>
import { computed } from 'vue'
import {
  ArrowDown, ArrowRight, Bell, Box, ChatDotRound, ChatLineRound, Coin,
  Collection, DataAnalysis, Delete, Document, EditPen, Fold, FolderOpened,
  Histogram, House, Link, Menu, Moon, More, Picture, Plus, PriceTag, Search,
  Sunny, SwitchButton, TopRight, TrendCharts, User, UserFilled, View, WarningFilled
} from '@element-plus/icons-vue'

const props = defineProps({ name: { type: String, default: '' } })
const icons = {
  'ti-layout-dashboard': DataAnalysis,
  'ti-article': Document,
  'ti-article-off': Document,
  'ti-pencil-plus': EditPen,
  'ti-pencil': EditPen,
  'ti-books': Collection,
  'ti-message-circle': ChatLineRound,
  'ti-message-circle-off': ChatLineRound,
  'ti-message-exclamation': WarningFilled,
  'ti-folders': FolderOpened,
  'ti-tags': PriceTag,
  'ti-photo-cog': Picture,
  'ti-photo': Picture,
  'ti-user-shield': UserFilled,
  'ti-user-circle': User,
  'ti-users': UserFilled,
  'ti-external-link': TopRight,
  'ti-arrow-up-right': TopRight,
  'ti-arrow-right': ArrowRight,
  'ti-menu-2': Menu,
  'ti-fold': Fold,
  'ti-search': Search,
  'ti-bell': Bell,
  'ti-sun': Sunny,
  'ti-moon': Moon,
  'ti-chevron-down': ArrowDown,
  'ti-logout': SwitchButton,
  'ti-chart-bar': Histogram,
  'ti-chart-line': TrendCharts,
  'ti-database-off': Coin,
  'ti-plus': Plus,
  'ti-eye': View,
  'ti-trash': Delete,
  'ti-home': House,
  'ti-link': Link,
  'ti-box': Box
}
const resolvedIcon = computed(() => icons[props.name] || More)
</script>

<template>
  <component :is="resolvedIcon" class="admin-icon" aria-hidden="true" />
</template>

<style scoped>
.admin-icon { width: 1em; height: 1em; display: block; fill: currentColor; }
</style>
