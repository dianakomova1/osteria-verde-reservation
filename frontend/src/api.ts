import type { Availability, Reservation, ReservationRequest } from './types'

const API_URL = import.meta.env.VITE_API_URL ?? '/api'

const getErrorMessage = async (response: Response) => {
  try {
    const data = await response.json()
    return data.message || 'Сервер не смог выполнить запрос'
  } catch {
    return 'Сервер не смог выполнить запрос'
  }
}

const request = async <T>(url: string, options?: RequestInit): Promise<T> => {
  const response = await fetch(`${API_URL}${url}`, {
    cache: 'no-store',
    ...options,
  })
  if (!response.ok) {
    throw new Error(await getErrorMessage(response))
  }
  return response.json()
}

export const checkAvailability = (
  date: string,
  time: string,
  guests: number,
  location: string,
) => {
  const query = new URLSearchParams({ date, time, guests: String(guests), location })
  return request<Availability>(`/reservations/availability?${query}`)
}

export const createReservation = (reservation: ReservationRequest) =>
  request<Reservation>('/reservations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reservation),
  })

export const findReservation = (bookingCode: string) =>
  request<Reservation>(`/reservations/${encodeURIComponent(bookingCode)}`)

export const cancelReservation = (bookingCode: string) =>
  request<Reservation>(`/reservations/${encodeURIComponent(bookingCode)}/cancel`, {
    method: 'PATCH',
  })
