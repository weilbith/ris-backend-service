<script lang="ts" setup>
import { ref, h, VNode } from "vue"
import FootnoteInput, { Footnote } from "./FootnoteInput.vue"
import FootnoteInputAsList from "./FootnoteInputAsList.vue"
import ExpandableDataSet from "@/components/ExpandableDataSet.vue"
import { withSummarizer } from "@/shared/components/DataSetSummary.vue"
import EditableList from "@/shared/components/EditableList.vue"

const EXAMPLE_FOOTNOTES: Footnote[] = [
  {
    prefix: "§ 7 Abs. 1a Satz 1 u. 2",
    parts: [
      {
        type: "Änderungsfußnote",
        content:
          "eine ganze Menge Text mit viel Inhalt über eine Zeile hinaus und noch viel viel viel weiter in die nächste",
      },
      {
        type: "EU/EG-Recht",
        content: "irgendwas halt",
      },
    ],
  },
  {
    parts: [
      { type: "Landesrecht", content: "" },
      { type: "Kommentierende Fußnote", content: "einfach nur ein Kommentar" },
      { type: "BVerfG-Entscheidung", content: "das wurde halt so entschieden" },
    ],
  },
  {
    prefix: "§ 1 Abs. 5b",
    parts: [
      {
        type: "Sonstige Fußnote",
        content:
          "noch etwas mehr oben drauf\nmit einer neuen Zeile\nund noch einer\n",
      },
      {
        type: "Sonstige Fußnote",
        content: "ach nochmal eben etwas",
      },
    ],
  },
]

const inputValueForCompletion = ref<Footnote[]>(
  JSON.parse(JSON.stringify(EXAMPLE_FOOTNOTES))
)
const inputValueForList = ref<Footnote[]>(
  JSON.parse(JSON.stringify(EXAMPLE_FOOTNOTES))
)
const inputValueForExamples = ref<Footnote[]>(
  JSON.parse(JSON.stringify(EXAMPLE_FOOTNOTES))
)

function addNewFootnote(): void {
  inputValueForList.value.push({ parts: [] })
}

function summarizeFootnotePart(
  part: {
    type?: string
    content?: string
  },
  extraTypeClasses = [""]
): (VNode | string)[] {
  const typeClasses = [
    "bg-yellow-300",
    "rounded",
    "px-6",
    "py-2",
    "whitespace-pre",
    ...extraTypeClasses,
  ]
  const type = h("span", { class: typeClasses }, part.type ?? "Unbekannt")

  const contentClasses = ["pl-6", "pr-10", "inline", "whitespace-pre-wrap"]
  const contentText = h("p", { class: contentClasses }, part.content?.trim())

  const contentEmptyHint = h(
    "span",
    { class: [...contentClasses, "text-gray-600"] },
    "leer"
  )
  const hasContent = part.content && part.content.trim().length > 0
  const content = hasContent ? contentText : contentEmptyHint

  return [type, content]
}

function summarizePrefix(prefix?: string): VNode | string {
  const prefixNode = h(
    "span",
    { class: "pr-10" },
    prefix?.trim().replaceAll(/\n/g, "<br>")
  )
  const hasPrefix = prefix && prefix.trim().length > 0
  return hasPrefix ? prefixNode : ""
}

function summarizeFootnote(footnote: Footnote): VNode {
  const partNodes = footnote.parts.map((part) => summarizeFootnotePart(part))
  const prefix = summarizePrefix(footnote.prefix)
  return h("span", { class: ["leading-30"] }, [prefix, partNodes])
}

function summarizeFootnotePerLine(footnote: Footnote): VNode {
  const partNodes = footnote.parts.map((part) =>
    h("div", summarizeFootnotePart(part))
  )
  const prefix = summarizePrefix(footnote.prefix)
  return h("div", { class: ["flex", "flex-col", "gap-10"] }, [
    prefix,
    partNodes,
  ])
}

const TYPE_TO_ABBREVIATION: Record<string, string> = {
  ["Änderungsfußnote"]: "ÄND",
  "Kommentierende Fußnote": "KOM",
  "BVerfG-Entscheidung": "BVerfG",
  Landesrecht: "LAND",
  "EU/EG-Recht": "EU/EG",
  "Sonstige Fußnote": "SO",
}

function summarizeFootnoteWithAbbreviation(footnote: Footnote): VNode {
  const partNodes = footnote.parts.map(({ type, content }) =>
    summarizeFootnotePart(
      { type: type && TYPE_TO_ABBREVIATION[type], content },
      ["font-bold", "px-[0.2rem]"]
    )
  )
  const prefix = summarizePrefix(footnote.prefix)
  return h("span", { class: ["leading-30"] }, [prefix, partNodes])
}

function summarizeFootnoteWithAbbreviationAndNoBackground(
  footnote: Footnote
): VNode {
  const partNodes = footnote.parts.map(({ type, content }) =>
    summarizeFootnotePart(
      { type: type && TYPE_TO_ABBREVIATION[type], content },
      ["font-bold", "!bg-transparent", "pl-0", "pr-0"]
    )
  )
  const prefix = summarizePrefix(footnote.prefix)
  return h("span", { class: ["leading-30"] }, [prefix, partNodes])
}

function summarizeFootnoteSemicolon(footnote: Footnote): string {
  const parts = footnote.parts
    .map(({ content }) => content)
    .filter((content) => (content ?? "").trim().length > 0)
    .join("; ")
  const prefix = footnote.prefix ? `${footnote.prefix.trim()}: ` : ""
  return prefix + parts
}

const FootnoteSummary = withSummarizer(summarizeFootnote)
const FootnotePerLineSummary = withSummarizer(summarizeFootnotePerLine)
const FootnoteWithAbbreviationSummary = withSummarizer(
  summarizeFootnoteWithAbbreviation
)
const FootnoteWithAbbreviationAndNoBackgroundSummary = withSummarizer(
  summarizeFootnoteWithAbbreviationAndNoBackground
)
const FootnoteWithSemicolonSummary = withSummarizer(summarizeFootnoteSemicolon)
</script>

<template>
  <div class="bg-gray-100">
    <div class="flex flex-col p-64 w-[60rem]">
      <ExpandableDataSet
        :data-set="inputValueForCompletion"
        :summary-component="FootnoteSummary"
        title="Fußnoten"
      >
        <EditableList
          v-model="inputValueForCompletion"
          :default-value="{ parts: [] }"
          :edit-component="FootnoteInput"
          :summary-component="FootnoteSummary"
        />
      </ExpandableDataSet>
    </div>

    <div class="flex flex-col p-64 w-[60rem]">
      <ExpandableDataSet
        :data-set="inputValueForList"
        :summary-component="FootnoteSummary"
        title="Fußnoten"
      >
        <template v-for="(_, index) in inputValueForList" :key="index">
          <FootnoteInputAsList v-model="inputValueForList[index]" />

          <hr class="my-16 text-blue-500 w-full" />
        </template>

        <button
          aria-label="Weitere Angabe"
          class="add-button bg-blue-300 focus:outline-4 font-bold gap-0.5 hover:bg-blue-800 hover:text-white inline-flex items-center leading-18 mt-16 outline-0 outline-blue-800 outline-none outline-offset-4 pr-[0.25rem] py-[0.125rem] text-14 text-blue-800 whitespace-nowrap"
          @click="addNewFootnote"
        >
          <span class="material-icons text-14">add</span>
          Weitere Fußnote
        </button>
      </ExpandableDataSet>
    </div>

    <div class="flex flex-col p-64 w-[60rem]">
      <ExpandableDataSet
        :data-set="inputValueForExamples"
        :summary-component="FootnotePerLineSummary"
        title="Fußnoten"
      >
        <EditableList
          v-model="inputValueForExamples"
          :default-value="{ parts: [] }"
          :edit-component="FootnoteInput"
          :summary-component="FootnotePerLineSummary"
        />
      </ExpandableDataSet>
    </div>

    <div class="flex flex-col p-64 w-[60rem]">
      <ExpandableDataSet
        :data-set="inputValueForExamples"
        :summary-component="FootnoteWithAbbreviationSummary"
        title="Fußnoten"
      >
        <EditableList
          v-model="inputValueForExamples"
          :default-value="{ parts: [] }"
          :edit-component="FootnoteInput"
          :summary-component="FootnoteWithAbbreviationSummary"
        />
      </ExpandableDataSet>
    </div>

    <div class="flex flex-col p-64 w-[60rem]">
      <ExpandableDataSet
        :data-set="inputValueForExamples"
        :summary-component="FootnoteWithAbbreviationAndNoBackgroundSummary"
        title="Fußnoten"
      >
        <EditableList
          v-model="inputValueForExamples"
          :default-value="{ parts: [] }"
          :edit-component="FootnoteInput"
          :summary-component="FootnoteWithAbbreviationAndNoBackgroundSummary"
        />
      </ExpandableDataSet>
    </div>

    <div class="flex flex-col p-64 w-[60rem]">
      <ExpandableDataSet
        :data-set="inputValueForExamples"
        :summary-component="FootnoteWithSemicolonSummary"
        title="Fußnoten"
      >
        <EditableList
          v-model="inputValueForExamples"
          :default-value="{ parts: [] }"
          :edit-component="FootnoteInput"
          :summary-component="FootnoteWithSemicolonSummary"
        />
      </ExpandableDataSet>
    </div>
  </div>
</template>
