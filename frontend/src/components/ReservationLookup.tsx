import { FormEvent, useState } from 'react'
import { cancelReservation, findReservation } from '../api'
import type { Reservation } from '../types'
import ReservationDetails from './ReservationDetails'

function ReservationLookup() {
  const [bookingCode, setBookingCode] = useState('')
  const [reservation, setReservation] = useState<Reservation | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSearch = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setIsLoading(true)
    setError('')
    setReservation(null)
    try {
      setReservation(await findReservation(bookingCode.trim().toUpperCase()))
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Не удалось найти бронь')
    } finally {
      setIsLoading(false)
    }
  }

  const handleCancel = async () => {
    if (!reservation || !window.confirm('Отменить это бронирование?')) return
    setIsLoading(true)
    setError('')
    try {
      setReservation(await cancelReservation(reservation.bookingCode))
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Не удалось отменить бронь')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <section className="lookup" id="lookup">
      <div className="lookup__content">
        <p className="eyebrow">Уже бронировали?</p>
        <h2>Найдите свою бронь</h2>
        <p>Введите номер из подтверждения, например OV-7K2M9P.</p>
        <form className="lookup__form" onSubmit={handleSearch}>
          <label>
            Номер брони
            <input
              required
              maxLength={9}
              placeholder="OV-XXXXXX"
              value={bookingCode}
              onChange={(event) => setBookingCode(event.target.value.toUpperCase())}
            />
          </label>
          <button className="button" disabled={isLoading} type="submit">
            {isLoading ? 'Ищем…' : 'Найти бронь'}
          </button>
        </form>
        {error && <p className="message message--error" role="alert">{error}</p>}

        {reservation && (
          <article className="lookup__result">
            <div className="lookup__result-heading">
              <div><small>Номер брони</small><strong>{reservation.bookingCode}</strong></div>
              {reservation.status === 'CONFIRMED' && (
                <button className="text-button text-button--danger" disabled={isLoading} type="button" onClick={handleCancel}>
                  Отменить бронь
                </button>
              )}
            </div>
            <ReservationDetails reservation={reservation} />
          </article>
        )}
      </div>
    </section>
  )
}

export default ReservationLookup
