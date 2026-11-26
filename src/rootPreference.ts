import type { Language } from './keyboard'

const storageKey = (language: Language) => `hooks.root.${language}`

export function loadRoot(language: Language, roots: readonly { value: string }[], getStorage: () => Storage = () => window.localStorage): string | null {
  try {
    const saved = getStorage().getItem(storageKey(language))
    if (saved === null) return null
    if (roots.some((root) => root.value === saved)) return saved
    console.warn(`Ignoring unsupported saved ${language} root: ${saved}`)
  } catch (error) {
    console.warn(`Unable to load the saved ${language} root`, error)
  }
  return null
}

export function saveRoot(language: Language, root: string, getStorage: () => Storage = () => window.localStorage): void {
  try {
    getStorage().setItem(storageKey(language), root)
  } catch (error) {
    console.warn(`Unable to save the selected ${language} root`, error)
  }
}
