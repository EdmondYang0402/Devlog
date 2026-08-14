const parsePlaylist = () => {
  try {
    const parsed = JSON.parse(String(import.meta.env.VITE_MUSIC_PLAYLIST_JSON || '[]'))
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

export const musicPlayerConfig = Object.freeze({
  title: String(import.meta.env.VITE_MUSIC_PLAYER_TITLE || '').trim() || 'DevLog BGM',
  playlist: Object.freeze(parsePlaylist())
})
