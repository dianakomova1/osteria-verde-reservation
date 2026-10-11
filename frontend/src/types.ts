export type ReservationRequest = {
  guestName: string
  phone: string
  email: string
  reservationDate: string
  reservationTime: string
  guestsCount: number
  seatingPreference: string
}

export type Reservation = ReservationRequest & {
  id: number
  bookingCode: string
  reservationEndTime: string
  status: 'CONFIRMED' | 'CANCELLED'
  tableNumber: number
  tableCapacity: number
  tableLocation: string
}

export type Availability = {
  available: boolean
  tableNumber: number | null
  tableCapacity: number | null
  tableLocation: string | null
  message: string
}
