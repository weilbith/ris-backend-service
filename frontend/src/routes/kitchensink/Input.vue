<script lang="ts" setup>
import { Document } from "@tiptap/extension-document"
import { History } from "@tiptap/extension-history"
import { Mention, MentionOptions } from "@tiptap/extension-mention"
import { Paragraph } from "@tiptap/extension-paragraph"
import { Text } from "@tiptap/extension-text"
import { EditorContent, JSONContent, Editor, VueRenderer } from "@tiptap/vue-3"
import tippy from "tippy.js"
import { watch } from "vue"
import CompletionMenu from "./CompletionMenu.vue"

interface Props {
  modelValue?: Section[]
}

interface Emits {
  (event: "update:modelValue", value: Section[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// TODO: use label and identifier
const CANIDATES = [
  "Änderungsfußnote",
  "Kommentierende Fußnote",
  "BVerfG-Entscheidung",
  "Landesrecht",
  "EU/EG-Recht",
  "Sonstige Fußnote",
]

const options: MentionOptions = {
  HTMLAttributes: {
    class: "bg-yellow-300 rounded px-10 py-2",
  },
  renderLabel({ node }) {
    return `${node.attrs.label}`
  },
  suggestion: {
    items: ({ query }) =>
      CANIDATES.filter((item) =>
        item.toLowerCase().startsWith(query.toLowerCase())
      ),
    // char: "/",
    render: () => {
      let component: VueRenderer
      let popup: ReturnType<typeof tippy>

      return {
        onStart: (props) => {
          component = new VueRenderer(CompletionMenu, {
            props,
            editor: props.editor,
          })

          if (props.clientRect) {
            popup = tippy("body", {
              getReferenceClientRect: props.clientRect,
              appendTo: () => document.body,
              content: component.element,
              showOnCreate: true,
              interactive: true,
              trigger: "manual",
              placement: "top-start",
            })
          }
        },

        onUpdate(props) {
          component.updateProps(props)

          if (props.clientRect) {
            popup[0].setProps({
              getReferenceClientRect: props.clientRect,
            })
          }
        },

        onKeyDown(props) {
          switch (props.event.key) {
            case "Escape":
              popup[0].hide()
              return true

            default:
              return component.ref?.onKeyDown(props)
          }
        },

        onExit() {
          popup[0].destroy()
          component.destroy()
        },
      }
    },
  },
}

const EDITOR_CLASSES = [
  "p-16",
  "border-2",
  "border-blue-800",
  "focus:outline-2",
  "hover:outline-2",
  "h-128",
  "outline-0",
  "outline-blue-800",
  "outline-none",
  "outline-offset-[-4px]",
  "input",
  "overflow-y-auto",
]

const editor = new Editor({
  editorProps: {
    attributes: {
      tabindex: "0",
      class: EDITOR_CLASSES.join(" "),
    },
  },
  content: "",
  extensions: [Document, Paragraph, Text, History, Mention.configure(options)],
  onUpdate: () => {
    const editorContent = editor.getJSON()
    const sections = parseEditorContentAsSections(editorContent)
    emit("update:modelValue", sections)
  },
})

function parseEditorContentAsSections(data?: JSONContent): Section[] {
  const paragraphs =
    data?.content?.filter(({ type }) => type == "paragraph") ?? []

  const paragraphsWithNewlines = paragraphs.map((paragraph, index) => {
    const content = paragraph.content ?? []
    index > 0 && content.unshift({ type: "text", text: "\n" })
    return { ...paragraph, content }
  })

  const allContentNodes = paragraphsWithNewlines.reduce(
    (nodes, paragraph) => [...nodes, ...(paragraph.content ?? [])],
    [] as JSONContent[]
  )

  const nodesWithMergedTextSequences = allContentNodes.reduce(
    (nodes, currentNode) => {
      const lastIndex = nodes.length - 1
      const lastNode = nodes[lastIndex] as JSONContent | undefined
      const shouldMergeWithLastNode =
        lastNode?.type == "text" && currentNode.type == "text"

      if (shouldMergeWithLastNode) {
        const text = (lastNode?.text ?? "") + currentNode.text
        const mergedNode = { ...lastNode, ...currentNode, text }
        nodes[lastIndex] = mergedNode
      } else {
        nodes.push(currentNode)
      }

      return nodes
    },
    [] as JSONContent[]
  )

  return nodesWithMergedTextSequences.map(({ type, attrs, text }) => ({
    type: type == Mention.name ? SectionType.KEYWORD : SectionType.TEXT,
    content: (attrs?.label as string) ?? text ?? "",
  }))
}

watch(
  () => props.modelValue,
  (value) => {
    const editorContent = parseSectionsAsEditorContent(value)
    const cursorPosition = editor.state.selection
    editor.commands.setContent(editorContent, false)
    editor.commands.setTextSelection(cursorPosition)
  },
  { immediate: true }
)

function parseSectionsAsEditorContent(sections?: Section[]): JSONContent {
  const allContentNodes = sections?.map((section): JSONContent => {
    const isKeyword = section.type == SectionType.KEYWORD
    const type = isKeyword ? Mention.name : "text"
    const content = isKeyword
      ? { attrs: { id: null, label: section.content } }
      : { text: section.content }

    return { type, ...content }
  })

  const nodesGroupedInParagraphs = allContentNodes?.reduce(
    (groups, node) => {
      const linesOfNode = node.text?.split("\n")
      const splitNodes = linesOfNode?.map((text) => ({ ...node, text }))
      const nodesToAdd = splitNodes ?? [node]

      groups[groups.length - 1].push(nodesToAdd[0])
      nodesToAdd.splice(1).forEach((node) => groups.push([node]))

      return groups
    },
    [[]] as JSONContent[][]
  )

  // TipTap throws error for empty text nodes. Such can be a result from the
  // former splitting across paragraphs.
  const nodeGroupdsWithoutEmptyTexts = nodesGroupedInParagraphs?.map((group) =>
    group.filter(({ text }) => text == undefined || text.length > 0)
  )

  const paragraphs = nodeGroupdsWithoutEmptyTexts?.map((nodeGroup) => ({
    type: "paragraph",
    content: nodeGroup,
  }))

  return { type: "doc", content: paragraphs }
}
</script>

<script lang="ts">
export enum SectionType {
  KEYWORD = "keyword",
  TEXT = "text",
}

export interface Section {
  type: SectionType
  content: string
}
</script>

<template>
  <div>
    <span class="text-gray-900">
      Sie können mit @ den Fußnoten-Typ wählen (z.B. Änderungsfußnote,
      Kommentierende Fußnote)
    </span>

    <EditorContent :editor="editor" />
  </div>
</template>
