import DocumentationOffice from "./documentationOffice"

export type DocumentUnitListEntry = {
  id: string
  uuid: string
  documentNumber: string
  creationTimestamp: string
  fileName?: string
  fileNumber?: string
  documentationOffice?: DocumentationOffice
}
