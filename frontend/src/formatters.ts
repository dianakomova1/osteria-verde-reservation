export const formatDate = (date: string) =>
  new Date(`${date}T00:00:00`).toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })

export const formatTime = (time: string) => time.slice(0, 5)

export const getMealName = (time: string) => {
  const hour = Number(time.split(':')[0])
  if (hour < 17) return 'обед'
  return 'ужин'
}
