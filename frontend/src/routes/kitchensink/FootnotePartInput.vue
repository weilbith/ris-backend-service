<script lang="ts" setup>
import { computed } from "vue"
import {
  FootnoteSectionType,
  FOOTNOTE_TYPE_TO_LABEL_MAPPING,
} from "./footnote_types"
import InputField from "@/shared/components/input/InputField.vue"
import TextInput from "@/shared/components/input/TextInput.vue"

interface FootnotePart {
  type?: string
  content?: string
}

interface Props {
  modelValue: FootnotePart
}

interface Emits {
  (event: "update:modelValue", value: FootnotePart): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const partType = computed({
  get: () =>
    (props.modelValue.type ??
      FootnoteSectionType.CHANGE_FOOTNOTE) as FootnoteSectionType, // =P
  set: (type: FootnoteSectionType) =>
    emit("update:modelValue", { type, content: props.modelValue.content }),
})

const content = computed({
  get: () => props.modelValue.content ?? "",
  set: (content: string) =>
    emit("update:modelValue", { content, type: props.modelValue.type }),
})
</script>

<template>
  <div>
    <InputField id="footnotePartType" label="Fußnotentyp">
      <div class="flex flex-wrap gap-12 justify-between mt-12">
        <div
          v-for="type in Object.keys(FootnoteSectionType)"
          :key="type"
          class="flex gap-8 items-center shrink-0 w-224"
        >
          <input
            :id="type"
            v-model="partType"
            :aria-label="type"
            name="footnoteType"
            type="radio"
            :value="type"
          />
          <span class="label-02-reg">{{
            FOOTNOTE_TYPE_TO_LABEL_MAPPING[type as FootnoteSectionType]
          }}</span>
        </div>
      </div>
    </InputField>

    <InputField id="footnotePartContent" label="Text">
      <TextInput
        id="footnotePartContent"
        v-model="content"
        aria-label="Fußnoten Text"
      />
    </InputField>
  </div>
</template>

<style scoped>
input[type="radio"] {
  display: grid;
  width: 1.5em;
  height: 1.5em;
  border: 0.15em solid currentcolor;
  border-radius: 50%;
  margin-right: 10px;
  appearance: none;
  background-color: white;
  color: #004b76;
  place-content: center;
}

input[type="radio"]::before {
  width: 0.75em;
  height: 0.75em;
  border-radius: 50%;
  background-color: #004b76;
  content: "";
  transform: scale(0);
}

input[type="radio"]:checked::before {
  transform: scale(1);
}
</style>
