export type Language = 'EN' | 'ES'

export const alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')
const spanishKeys = ['1', '2', '3', '4']
const spanishAlphabet = alphabet.filter((key) => key !== 'K' && key !== 'W')
const spanishLabels: Record<string, string> = { '1': 'CH', '2': 'LL', '3': 'Ñ', '4': 'RR' }

export function keyboardKeys(language: Language): string[] {
  return language === 'ES' ? [...spanishAlphabet, ...spanishKeys] : alphabet
}

export function keyLabel(key: string): string {
  return spanishLabels[key] ?? key
}

export function displayWord(word: string): string {
  return [...word].map(keyLabel).join('')
}

export function physicalKey(key: string, language: Language): string | undefined {
  const upper = key.toUpperCase()
  const normalized = language === 'ES' && upper === 'Ñ' ? '3' : upper
  return keyboardKeys(language).includes(normalized) ? normalized : undefined
}
