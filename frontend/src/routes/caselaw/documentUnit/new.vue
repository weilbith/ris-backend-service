<script setup lang="ts">
import { onBeforeMount } from "vue"
import { useRouter } from "vue-router"
import documentUnitService from "@/services/documentUnitService"

const router = useRouter()

onBeforeMount(async () => {
  const createResponse = await documentUnitService.createNew("KO", "RE")
  if (createResponse.data)
    await router.replace({
      name: "caselaw-documentUnit-:documentNumber-files",
      params: { documentNumber: createResponse.data.documentNumber },
    })
})
</script>

<template>
  <div></div>
</template>
