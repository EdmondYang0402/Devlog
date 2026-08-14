import { computed } from 'vue'

export const randomBetween = (min, max) => min + Math.random() * (max - min)
export const randomItem = items => items[Math.floor(Math.random() * items.length)]

export const useEffectParticles = ({ type, counts, densityTier, densityScale, createParticle }) => {
  const particles = computed(() => {
    const baseCount = counts[densityTier.value] || counts.desktop
    const count = Math.max(1, Math.round(baseCount * densityScale.value))
    return Array.from({ length: count }, (_, index) => ({
      id: `${type}-${densityTier.value}-${index}`,
      ...createParticle(index, count)
    }))
  })

  return { particles }
}
