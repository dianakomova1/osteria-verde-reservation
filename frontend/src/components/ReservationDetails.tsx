import { formatDate, formatTime } from '../formatters'
import type { Reservation } from '../types'

type ReservationDetailsProps = {
  reservation: Reservation
}

function ReservationDetails({ reservation }: ReservationDetailsProps) {
  return (
    <dl className="reservation-details">
      <div><dt>Дата</dt><dd>{formatDate(reservation.reservationDate)}</dd></div>
      <div><dt>Время</dt><dd>{formatTime(reservation.reservationTime)}–{formatTime(reservation.reservationEndTime)}</dd></div>
      <div><dt>Гостей</dt><dd>{reservation.guestsCount}</dd></div>
      <div><dt>Столик</dt><dd>№{reservation.tableNumber}, {reservation.tableLocation}</dd></div>
      <div><dt>Гость</dt><dd>{reservation.guestName}</dd></div>
      <div><dt>Статус</dt><dd className={`status status--${reservation.status.toLowerCase()}`}>{reservation.status === 'CONFIRMED' ? 'Подтверждена' : 'Отменена'}</dd></div>
    </dl>
  )
}

export default ReservationDetails
