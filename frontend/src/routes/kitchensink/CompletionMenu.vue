<script lang="ts" setup>
import { ref, watch, defineExpose } from "vue"

interface Props {
  items: any[]
  command: (options: { label: string }) => void
}
const props = defineProps<Props>()
const selectedIndex = ref(0)

watch(() => props.items, resetSelection)

function resetSelection(): void {
  selectedIndex.value = 0
}

function selectNext(): void {
  selectedIndex.value = (selectedIndex.value + 1) % props.items.length
}

function selectPrevious(): void {
  selectedIndex.value =
    (selectedIndex.value + props.items.length - 1) % props.items.length
}

function selectItem(index: number): void {
  const item = props.items[index]
  props.command({ label: item })
}

function onKeyDown({ event }: { event: KeyboardEvent }) {
  switch (event.key) {
    case "ArrowUp":
      selectPrevious()
      return true

    case "ArrowDown":
      selectNext()
      return true

    case "Enter":
      selectItem(selectedIndex.value)
      event.stopPropagation()
      return true

    default:
      return false
  }
}

defineExpose({ onKeyDown })
</script>

<template>
  <div
    class="bg-white border-1 border-gray-400 drop-shadow-xl flex flex-col p-4"
  >
    <template v-if="items.length">
      <button
        v-for="(item, index) in items"
        :key="index"
        class="border-1 border-transparent item px-4 py-2 rounded text-left"
        :class="{ '!border-gray-800': index == selectedIndex }"
        @click="selectItem(index)"
      >
        {{ item }}
      </button>
    </template>

    <div v-else class="item">No result</div>
  </div>
</template>
