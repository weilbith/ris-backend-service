<script setup lang="ts">
import { computed } from "vue"
import FieldOfLawMain from "@/components/FieldOfLawMain.vue"
import KeyWords from "@/components/KeyWords.vue"
import Norms from "@/components/NormReferences.vue"
import { ContentRelatedIndexing } from "@/domain/documentUnit"

const props = defineProps<{
  documentUnitUuid: string
  modelValue: ContentRelatedIndexing
}>()

const emit = defineEmits<{
  (event: "update:modelValue", value?: ContentRelatedIndexing): void
}>()

const contentRelatedIndexing = computed({
  get: () => {
    return props.modelValue
  },
  set: (value) => {
    if (value) emit("update:modelValue", value)
  },
})
</script>

<template>
  <div class="mb-[4rem]">
    <h1 class="heading-02-regular mb-[1rem]">Inhaltliche Erschließung</h1>
    <KeyWords :document-unit-uuid="props.documentUnitUuid" />
    <FieldOfLawMain :document-unit-uuid="props.documentUnitUuid" />
    <Norms v-model="contentRelatedIndexing.norms" />
    <!-- Aktivzitierung -->
  </div>
</template>
