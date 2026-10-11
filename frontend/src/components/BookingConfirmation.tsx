import { useState } from 'react'
import { cancelReservation } from '../api'
import { getMealName } from '../formatters'
import type { Reservation } from '../types'
import Logo from './Logo'
import ReservationDetails from './ReservationDetails'

type BookingConfirmationProps = {
  reservation: Reservation
  onBack: () => void
  onReservationChange: (reservation: Reservation) => void
}

function BookingConfirmation({ reservation, onBack, onReservationChange }: BookingConfirmationProps) {
  const [error, setError] = useState('')
  const [isCancelling, setIsCancelling] = useState(false)

  const handleCancel = async () => {
    if (!window.confirm('Отменить это бронирование?')) return
    setIsCancelling(true)
    setError('')
    try {
      onReservationChange(await cancelReservation(reservation.bookingCode))
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Не удалось отменить бронь')
    } finally {
      setIsCancelling(false)
    }
  }

  return (
    <main className="confirmation" id="top">
      <section className="confirmation__card">
        <Logo dark />
        <p className={`confirmation__mark ${reservation.status === 'CANCELLED' ? 'confirmation__mark--cancelled' : ''}`} aria-hidden="true">
          {reservation.status === 'CONFIRMED' ? '✓' : '×'}
        </p>
        <p className="eyebrow">{reservation.status === 'CONFIRMED' ? 'Бронирование подтверждено' : 'Бронирование отменено'}</p>
        <h1>
          {reservation.status === 'CONFIRMED'
            ? `${reservation.guestName}, ждём вас на ${getMealName(reservation.reservationTime)}!`
            : 'Бронь успешно отменена'}
        </h1>
        <p className="confirmation__text">
          Сохраните номер брони: <strong>{reservation.bookingCode}</strong>. По нему можно проверить детали на главной странице.
        </p>

        <ReservationDetails reservation={reservation} />

        {error && <p className="message message--error" role="alert">{error}</p>}
        <div className="confirmation__actions">
          <button className="button" type="button" onClick={onBack}>Вернуться на главную</button>
          {reservation.status === 'CONFIRMED' && (
            <button className="text-button text-button--danger" disabled={isCancelling} type="button" onClick={handleCancel}>
              {isCancelling ? 'Отменяем…' : 'Отменить бронь'}
            </button>
          )}
        </div>
      </section>
    </main>
  )
}

export default BookingConfirmation
