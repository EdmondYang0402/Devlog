import { reactive } from 'vue'

export const useDisclosureSet = (initialKeys = []) => {
  const expandedKeys = reactive(new Set(initialKeys))

  const isExpanded = key => expandedKeys.has(key)

  const toggle = key => {
    expandedKeys.has(key) ? expandedKeys.delete(key) : expandedKeys.add(key)
  }

  return { expandedKeys, isExpanded, toggle }
}
