export enum FootnoteSectionType {
  CHANGE_FOOTNOTE = "CHANGE_FOOTNOTE",
  COMMENT_FOOTNOTE = "COMMENT_FOOTNOTE",
  COURT_DECISION = "COURT_DECISION",
  FEDERAL_LAW = "FEDERAL_LAW",
  EUROPEAN_UNION_LAW = "EUROPEAN_UNION_LAW",
  OTHER_FOOTNOTE = "OTHER_FOOTNOTE",
}

export type FootnoteSection = {
  type?: FootnoteSectionType
  content?: string
}

export type Footnote = {
  prefix?: string // TODO: Remove again as declined by user.
  parts: FootnoteSection[]
}

export const FOOTNOTE_TYPE_TO_LABEL_MAPPING: Record<
  FootnoteSectionType,
  string
> = {
  [FootnoteSectionType.CHANGE_FOOTNOTE]: "Änderungsfußnote",
  [FootnoteSectionType.COMMENT_FOOTNOTE]: "Kommentierende Fußnote",
  [FootnoteSectionType.COURT_DECISION]: "BVerfG-Entscheidung",
  [FootnoteSectionType.FEDERAL_LAW]: "Landesrecht",
  [FootnoteSectionType.EUROPEAN_UNION_LAW]: "EU/EG-Recht",
  [FootnoteSectionType.OTHER_FOOTNOTE]: "Sonstige Fußnote",
}
