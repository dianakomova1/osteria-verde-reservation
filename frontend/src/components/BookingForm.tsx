import { FormEvent, useState } from 'react'
import { checkAvailability, createReservation } from '../api'
import type { Availability, Reservation, ReservationRequest } from '../types'

type BookingFormProps = {
  onCreated: (reservation: Reservation) => void
}

const initialForm: ReservationRequest = {
  guestName: '',
  phone: '',
  email: '',
  reservationDate: '',
  reservationTime: '',
  guestsCount: 2,
  seatingPreference: 'Не имеет значения',
}

const getToday = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const timeSlots = Array.from({ length: 19 }, (_, index) => {
  const minutes = 12 * 60 + index * 30
  return `${String(Math.floor(minutes / 60)).padStart(2, '0')}:${String(minutes % 60).padStart(2, '0')}`
})

function BookingForm({ onCreated }: BookingFormProps) {
  const [form, setForm] = useState(initialForm)
  const [availability, setAvailability] = useState<Availability | null>(null)
  const [isChecking, setIsChecking] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState('')

  const updateField = (field: keyof ReservationRequest, value: string | number) => {
    setForm((current) => ({ ...current, [field]: value }))
    if (['reservationDate', 'reservationTime', 'guestsCount', 'seatingPreference'].includes(field)) {
      setAvailability(null)
    }
    setError('')
  }

  const handleCheck = async () => {
    if (!form.reservationDate || !form.reservationTime) {
      setError('Сначала выберите дату и время')
      return
    }

    setIsChecking(true)
    setError('')
    try {
      setAvailability(await checkAvailability(
        form.reservationDate,
        form.reservationTime,
        form.guestsCount,
        form.seatingPreference,
      ))
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Не удалось проверить наличие столиков')
    } finally {
      setIsChecking(false)
    }
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!availability?.available) {
      setError('Перед бронированием проверьте наличие столика')
      return
    }

    setIsSubmitting(true)
    setError('')
    try {
      onCreated(await createReservation(form))
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } catch (requestError) {
      setAvailability(null)
      setError(requestError instanceof Error ? requestError.message : 'Не удалось создать бронирование')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <section className="booking section" id="booking">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Онлайн-бронирование</p>
          <h2>Найдём подходящий столик</h2>
        </div>
        <p>Сначала выберите параметры визита, затем оставьте контакты для подтверждения.</p>
      </div>

      <form className="booking-form" onSubmit={handleSubmit}>
        <fieldset>
          <legend><span>1</span> Параметры визита</legend>
          <div className="form-grid">
            <label>
              Дата
              <input
                required
                type="date"
                min={getToday()}
                value={form.reservationDate}
                onChange={(event) => updateField('reservationDate', event.target.value)}
              />
            </label>
            <label>
              Время
              <select
                required
                value={form.reservationTime}
                onChange={(event) => updateField('reservationTime', event.target.value)}
              >
                <option value="">Выберите время</option>
                {timeSlots.map((time) => <option key={time} value={time}>{time}</option>)}
              </select>
            </label>
            <label>
              Количество гостей
              <input
                required
                min="1"
                max="12"
                type="number"
                value={form.guestsCount}
                onChange={(event) => updateField('guestsCount', Number(event.target.value))}
              />
            </label>
            <label>
              Желаемое расположение
              <select
                value={form.seatingPreference}
                onChange={(event) => updateField('seatingPreference', event.target.value)}
              >
                <option>Не имеет значения</option>
                <option>У окна</option>
                <option>Основной зал</option>
                <option>На втором этаже</option>
                <option>На мягких диванах</option>
                <option>На веранде</option>
              </select>
            </label>
          </div>
          <button className="button button--outline" disabled={isChecking} type="button" onClick={handleCheck}>
            {isChecking ? 'Проверяем…' : 'Проверить свободный столик'}
          </button>

          {availability && (
            <div className={`message ${availability.available ? 'message--success' : 'message--error'}`} role="status">
              <strong>{availability.message}</strong>
              {availability.available && (
                <span>
                  Предварительно подойдёт столик №{availability.tableNumber} на {availability.tableCapacity} мест,
                  расположение — {availability.tableLocation?.toLowerCase()}.
                </span>
              )}
            </div>
          )}
        </fieldset>

        <fieldset disabled={!availability?.available}>
          <legend><span>2</span> Контактные данные</legend>
          <div className="form-grid">
            <label>
              Ваше имя
              <input
                required
                autoComplete="name"
                placeholder="Анна"
                value={form.guestName}
                onChange={(event) => updateField('guestName', event.target.value)}
              />
            </label>
            <label>
              Телефон
              <input
                required
                type="tel"
                inputMode="tel"
                autoComplete="tel"
                placeholder="+79991234567"
                pattern="\+7[0-9]{10}"
                title="Введите номер в формате +79991234567"
                value={form.phone}
                onChange={(event) => updateField('phone', event.target.value)}
              />
            </label>
            <label className="form-grid__wide">
              Email
              <input
                required
                type="email"
                autoComplete="email"
                placeholder="name@example.ru"
                value={form.email}
                onChange={(event) => updateField('email', event.target.value)}
              />
            </label>
          </div>
          <button className="button button--full" disabled={isSubmitting} type="submit">
            {isSubmitting ? 'Создаём бронь…' : 'Подтвердить бронирование'}
          </button>
          <p className="form-note">Бронь рассчитана на 2 часа. Регистрация на сайте не требуется.</p>
        </fieldset>

        {error && <p className="message message--error booking-form__error" role="alert">{error}</p>}
      </form>
    </section>
  )
}

export default BookingForm
