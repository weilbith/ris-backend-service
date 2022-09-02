import { expect } from "@playwright/test"
import { Page } from "playwright"

export const navigateToCategories = async (
  page: Page,
  documentNumber: string
) => {
  await page.goto(`/jurisdiction/docunit/${documentNumber}/categories`)
  await expect(page.locator("text=Spruchkörper")).toBeVisible()
}

export const navigateToFiles = async (page: Page, documentNumber: string) => {
  await page.goto(`/jurisdiction/docunit/${documentNumber}/files`)
  await expect(page.locator("h2:has-text('Dokumente')")).toBeVisible()
}

export const uploadTestfile = async (page: Page, filename: string) => {
  const [fileChooser] = await Promise.all([
    page.waitForEvent("filechooser"),
    page.locator("text=Festplatte durchsuchen").click(),
  ])
  await fileChooser.setFiles("./test/e2e/testfiles/" + filename)
  expect(page.locator("text=Upload läuft")).not.toBeVisible()
  expect(page.locator("text=Dokument wird geladen.")).not.toBeVisible()
}

export const isInViewport = (page: Page, selector: string, inside: boolean) => {
  return page.locator(selector).evaluate((element, inside) => {
    return new Promise((resolve) => {
      let observer: IntersectionObserver | undefined
      const timeout: ReturnType<typeof setTimeout> = setTimeout(() => {
        stopObserving(false)
      }, 20 * 1000)

      function stopObserving(result: boolean) {
        if (observer) {
          observer.disconnect()
          observer = undefined
        }
        clearTimeout(timeout)
        resolve(result)
      }

      function onIntersection(entries: { isIntersecting: boolean }[]) {
        if (entries[0].isIntersecting == inside) {
          stopObserving(true)
        }
      }

      observer = new IntersectionObserver(onIntersection)
      observer.observe(element)
      requestAnimationFrame(() => {
        // Firefox does not call IntersectionObserver without request animation frames
      })
    })
  }, inside)
}
