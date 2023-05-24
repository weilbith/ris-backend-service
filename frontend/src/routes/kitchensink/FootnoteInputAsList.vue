<script lang="ts" setup>
import { computed, h, VNode } from "vue"
import FootnotePartInput from "./FootnotePartInput.vue"
import { withSummarizer } from "@/shared/components/DataSetSummary.vue"
import EditableList from "@/shared/components/EditableList.vue"

type FootnotePart = {
  type?: string
  content?: string
}

type Footnote = {
  parts: FootnotePart[]
}

interface Props {
  modelValue: Footnote
}

interface Emits {
  (event: "update:modelValue", value: Footnote): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const parts = computed({
  get: () => props.modelValue.parts,
  set: (parts: FootnotePart[]) => emit("update:modelValue", { parts }),
})

// COPY-PASTE
function summarizeFootnotePart(part: {
  type?: string
  content?: string
}): VNode {
  const type = h(
    "span",
    { class: ["bg-yellow-300", "rounded", "px-10", "py-2"] },
    part.type ?? "Unbekannt"
  )

  const hasContent = part.content && part.content.trim().length > 0
  const content = h(
    "span",
    { class: { "text-gray-600": !hasContent } },
    hasContent ? part.content?.trim() : "leer"
  )

  return h("span", { class: ["flex", "gap-4"] }, [type, content])
}

const FootnotePartSummary = withSummarizer(summarizeFootnotePart)
</script>

<template>
  <EditableList
    v-model="parts"
    add-entry-label="Weiterer Teileintrag"
    class="border-l-4 border-l-gray-600 pl-20"
    :default-value="{}"
    :edit-component="FootnotePartInput"
    no-horizontal-separators
    :summary-component="FootnotePartSummary"
  />
</template>
