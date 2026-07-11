import { FormEvent, useState } from 'react'

type ReservationForm = {
  guestName: string
  phone: string
  email: string
  reservationDate: string
  reservationTime: string
  guestsCount: number
  seatingPreference: string
}

type ReservationResponse = ReservationForm & {
  id: number
  bookingCode: string
  status: string
}

const initialForm: ReservationForm = {
  guestName: '',
  phone: '',
  email: '',
  reservationDate: '',
  reservationTime: '',
  guestsCount: 2,
  seatingPreference: 'Не имеет значения',
}

const getMealName = (time: string) => {
  const hour = Number(time.split(':')[0])

  if (hour < 12) return 'завтрак'
  if (hour < 17) return 'обед'
  return 'ужин'
}

const formatDate = (date: string) =>
  new Date(`${date}T00:00:00`).toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })

function App() {
  const [form, setForm] = useState(initialForm)
  const [reservation, setReservation] = useState<ReservationResponse | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState('')

  const updateField = (field: keyof ReservationForm, value: string | number) => {
    setForm((current) => ({ ...current, [field]: value }))
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setIsLoading(true)
    setError('')

    try {
      const response = await fetch('http://localhost:8081/api/reservations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })

      if (!response.ok) {
        throw new Error('Не удалось создать бронирование')
      }

      setReservation(await response.json())
      setForm(initialForm)
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Неизвестная ошибка')
    } finally {
      setIsLoading(false)
    }
  }

  if (reservation) {
    return (
      <main className="confirmation">
        <section className="confirmation__card">
          <a className="logo confirmation__logo" href="#" onClick={() => setReservation(null)}>
            <span>OSTERIA <i className="logo__olive" aria-hidden="true" /> VERDE</span>
            <small>итальянский ресторан</small>
          </a>

          <p className="confirmation__mark" aria-hidden="true">✓</p>
          <p className="eyebrow">Бронирование подтверждено</p>
          <h1>{reservation.guestName}, благодарим за онлайн-бронирование!</h1>
          <p className="confirmation__text">
            Уже ждём вас на {getMealName(reservation.reservationTime)} в нашем ресторане.
          </p>

          <dl className="confirmation__details">
            <div>
              <dt>Дата и время</dt>
              <dd>{formatDate(reservation.reservationDate)}, {reservation.reservationTime.slice(0, 5)}</dd>
            </div>
            <div>
              <dt>Количество гостей</dt>
              <dd>{reservation.guestsCount}</dd>
            </div>
            <div>
              <dt>Расположение</dt>
              <dd>{reservation.seatingPreference}</dd>
            </div>
            <div>
              <dt>Номер брони</dt>
              <dd>{reservation.bookingCode}</dd>
            </div>
          </dl>

          <div className="confirmation__actions">
            <a
              className="primary-link"
              href="https://osteriamario.ru/menu/"
              target="_blank"
              rel="noreferrer"
            >
              Посмотреть меню Osteria Verde
            </a>
            <button className="confirmation__back" type="button" onClick={() => setReservation(null)}>
              Вернуться на главную
            </button>
          </div>
          <small className="confirmation__note">Меню откроется на официальном сайте Osteria Mario.</small>
        </section>
      </main>
    )
  }

  return (
    <main>
      <section className="hero">
        <nav className="navigation">
          <a className="logo" href="#top">
            <span>OSTERIA <i className="logo__olive" aria-hidden="true" /> VERDE</span>
            <small>итальянский ресторан</small>
          </a>
          <a className="navigation__link" href="#booking">Забронировать</a>
        </nav>

        <div className="hero__content" id="top">
          <div>
            <p className="eyebrow">Ужин начинается здесь</p>
            <h1>Стол для вашего идеального вечера</h1>
            <p className="hero__text">
              Выберите удобную дату и время — подтверждение бронирования займёт меньше минуты.
            </p>
            <a className="primary-link" href="#booking">Выбрать стол</a>
          </div>
          <img
            className="hero__image"
            src="/images/osteria-verde-interior.png"
            alt="Уютный зал итальянского ресторана"
          />
        </div>
      </section>

      <section className="booking" id="booking">
        <div className="booking__intro">
          <p className="eyebrow">Бронирование</p>
          <h2>Ваш уютный стол уже ждёт</h2>
          <p>После отправки формы мы создадим бронь и покажем её уникальный номер.</p>
        </div>

        <form className="booking-form" onSubmit={handleSubmit}>
          <label>
            Ваше имя
            <input required value={form.guestName} onChange={(event) => updateField('guestName', event.target.value)} />
          </label>
          <label>
            Телефон
            <input
              required
              type="tel"
              inputMode="tel"
              placeholder="+79991234567"
              pattern="\+7[0-9]{10}"
              title="Введите номер в формате +79991234567"
              value={form.phone}
              onChange={(event) => updateField('phone', event.target.value)}
            />
          </label>
          <label>
            Email
            <input
              required
              type="email"
              placeholder="name@example.ru"
              pattern="[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}"
              title="Введите email в формате name@example.ru"
              value={form.email}
              onChange={(event) => updateField('email', event.target.value)}
            />
          </label>
          <label>
            Количество гостей
            <input required min="1" max="12" type="number" value={form.guestsCount} onChange={(event) => updateField('guestsCount', Number(event.target.value))} />
          </label>
          <label>
            Дата
            <input required type="date" value={form.reservationDate} onChange={(event) => updateField('reservationDate', event.target.value)} />
          </label>
          <label>
            Время
            <input required type="time" value={form.reservationTime} onChange={(event) => updateField('reservationTime', event.target.value)} />
          </label>
          <label className="booking-form__wide">
            Предпочтение по расположению
            <select value={form.seatingPreference} onChange={(event) => updateField('seatingPreference', event.target.value)}>
              <option>Не имеет значения</option>
              <option>У окна</option>
              <option>На втором этаже</option>
              <option>На мягких диванах</option>
              <option>На веранде</option>
            </select>
          </label>

          <button disabled={isLoading} type="submit">
            {isLoading ? 'Создаём бронь…' : 'Подтвердить бронирование'}
          </button>
          {error && <p className="message message--error">{error}</p>}
        </form>
      </section>
    </main>
  )
}

export default App
