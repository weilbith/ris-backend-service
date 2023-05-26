<script lang="ts" setup>
import { computed, h, VNode } from "vue"
import FootnotePartInput from "./FootnotePartInput.vue"
import {
  FootnoteSection,
  FOOTNOTE_TYPE_TO_LABEL_MAPPING,
} from "@/routes/kitchensink/footnote_types"
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
function summarizeFootnotePart(
  part: FootnoteSection,
  extraTypeClasses = [""]
): VNode {
  const typeClasses = [
    "bg-yellow-300",
    "rounded",
    "px-6",
    "py-2",
    "whitespace-pre",
    ...extraTypeClasses,
  ]
  const typeLabel =
    (part.type && FOOTNOTE_TYPE_TO_LABEL_MAPPING[part.type]) ?? "Unbekannt"
  const type = h("span", { class: typeClasses }, typeLabel)

  const contentClasses = ["pl-6", "pr-10", "inline", "whitespace-pre-wrap"]
  const contentText = h("p", { class: contentClasses }, part.content?.trim())

  const contentEmptyHint = h(
    "span",
    { class: [...contentClasses, "text-gray-600"] },
    "leer"
  )
  const hasContent = part.content && part.content.trim().length > 0
  const content = hasContent ? contentText : contentEmptyHint

  return h("div", [type, content])
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
