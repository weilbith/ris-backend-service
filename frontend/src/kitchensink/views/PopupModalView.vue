<script lang="ts" setup>
import { ref } from "vue"
import TextButton from "@/shared/components/input/TextButton.vue"
import PopupModal from "@/shared/components/PopupModal.vue"

const showModal = ref(false)
const toggleModal = () => {
  showModal.value = !showModal.value
}
</script>

<template>
  <div>
    <div class="flex flex-col gap-y-20">
      <div>
        <TextButton
          button-type="primary"
          label="Open Modal"
          @click="toggleModal"
        />
      </div>
    </div>
    <PopupModal
      v-if="showModal"
      aria-label="Popup Modal"
      cancel-button-type="ghost"
      confirm-button-type="secondary"
      confirm-text="Bestätigen"
      content-text="This is Popup Modal content"
      header-text="Popup Modal"
      @close-modal="toggleModal"
      @confirm-action="toggleModal"
    />
  </div>
</template>
