<template>
  <div class="pagination" v-if="totalPages > 1">
    <button class="page-btn" :disabled="page <= 1" @click="$emit('change', page - 1)">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"/></svg>
    </button>
    <button
      v-for="p in visiblePages"
      :key="p"
      class="page-btn"
      :class="{ active: p === page }"
      @click="$emit('change', p)"
    >{{ p }}</button>
    <button class="page-btn" :disabled="page >= totalPages" @click="$emit('change', page + 1)">
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"/></svg>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 }
})

defineEmits(['change'])

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))

const visiblePages = computed(() => {
  const pages = []
  const t = totalPages.value
  const p = props.page
  let start = Math.max(1, p - 2)
  let end = Math.min(t, p + 2)
  if (end - start < 4) {
    if (start === 1) end = Math.min(t, start + 4)
    else start = Math.max(1, end - 4)
  }
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})
</script>
