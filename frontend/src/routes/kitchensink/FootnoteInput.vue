<script lang="ts" setup>
import { computed } from "vue"
import Input, { SectionType, Section } from "./Input.vue"

interface Props {
  modelValue?: Footnote
}

interface Emits {
  (event: "update:modelValue", value?: Footnote): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({ parts: [] }),
})

const emit = defineEmits<Emits>()

const inputValue = computed({
  get: () => parseFootnoteAsSections(props.modelValue),
  set: (sections: Section[]) =>
    emit("update:modelValue", parseSectionsAsFootnote(sections)),
})

function parseFootnoteAsSections(footnote: Footnote): Section[] {
  const sections = footnote.parts
    .map((part): Section[] => {
      const sections = []
      part.type &&
        sections.push({ type: SectionType.KEYWORD, content: part.type })
      part.content &&
        sections.push({ type: SectionType.TEXT, content: part.content })
      return sections
    })
    .reduce(
      (allSections, currentValue) => [...allSections, ...currentValue],
      []
    )

  footnote.prefix &&
    sections.unshift({ type: SectionType.TEXT, content: footnote.prefix })
  return sections
}

function parseSectionsAsFootnote(sections: Section[]): Footnote {
  const footnote: Footnote = { parts: [] }
  let partIndex = 0

  if (sections.length > 0 && sections[0].type == SectionType.TEXT) {
    footnote.prefix = sections[0].content
    partIndex = 1
  }

  while (partIndex < sections.length) {
    const section = sections[partIndex]
    const hasType = section.type == SectionType.KEYWORD
    const type = hasType ? section.content : undefined

    const nextSection = sections[partIndex + 1] ?? ({} as Section)
    const hasContent = nextSection.type == SectionType.TEXT
    const content = hasContent ? nextSection.content : undefined

    footnote.parts.push({ type, content })
    partIndex += hasContent ? 2 : 1
  }

  return footnote
}
</script>

<script lang="ts">
export type FootnoteSection = {
  type?: string
  content?: string
}

export type Footnote = {
  prefix?: string
  parts: FootnoteSection[]
}
</script>

<template>
  <Input v-model="inputValue" />
</template>
