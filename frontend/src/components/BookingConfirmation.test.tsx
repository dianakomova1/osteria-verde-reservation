import { createElement } from 'react'
import { renderToStaticMarkup } from 'react-dom/server'
import { describe, expect, it } from 'vitest'
import type { Reservation } from '../types'
import BookingConfirmation from './BookingConfirmation'

const reservation: Reservation = {
  id: 1,
  bookingCode: 'OV-ABC123',
  guestName: 'Анна',
  phone: '+79991234567',
  email: 'anna@example.ru',
  reservationDate: '2030-05-20',
  reservationTime: '18:30:00',
  reservationEndTime: '20:30:00',
  guestsCount: 4,
  seatingPreference: 'У окна',
  status: 'CONFIRMED',
  tableNumber: 3,
  tableCapacity: 4,
  tableLocation: 'Основной зал',
}

describe('BookingConfirmation', () => {
  it('показывает номер брони и основные детали', () => {
    const html = renderToStaticMarkup(
      createElement(BookingConfirmation, {
        reservation,
        onBack: () => undefined,
        onReservationChange: () => undefined,
      }),
    )

    expect(html).toContain('OV-ABC123')
    expect(html).toContain('Анна')
    expect(html).toContain('№3')
    expect(html).toContain('Основной зал')
    expect(html).toContain('Подтверждена')
  })
})
