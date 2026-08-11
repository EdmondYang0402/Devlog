const rawPlaylistId = String(import.meta.env.VITE_NETEASE_PLAYLIST_ID || '').trim()
const playlistId = /^\d+$/.test(rawPlaylistId) ? rawPlaylistId : ''

export const neteaseMusicConfig = Object.freeze({
  enabled: Boolean(playlistId),
  playlistId,
  title: String(import.meta.env.VITE_NETEASE_MUSIC_TITLE || '').trim() || 'DevLog BGM',
  playerHeight: 430
})

export const createNeteasePlaylistUrl = (id = neteaseMusicConfig.playlistId) => {
  const normalizedId = String(id || '').trim()
  if (!/^\d+$/.test(normalizedId)) return ''

  const url = new URL('https://music.163.com/outchain/player')
  url.searchParams.set('type', '0')
  url.searchParams.set('id', normalizedId)
  url.searchParams.set('auto', '0')
  url.searchParams.set('height', String(neteaseMusicConfig.playerHeight))
  return url.toString()
}
