import { useState } from 'react'
import BookingConfirmation from './components/BookingConfirmation'
import BookingForm from './components/BookingForm'
import Footer from './components/Footer'
import Header from './components/Header'
import Hero from './components/Hero'
import ReservationLookup from './components/ReservationLookup'
import type { Reservation } from './types'

function App() {
  const [reservation, setReservation] = useState<Reservation | null>(null)

  if (reservation) {
    return (
      <BookingConfirmation
        reservation={reservation}
        onBack={() => setReservation(null)}
        onReservationChange={setReservation}
      />
    )
  }

  return (
    <>
      <Header />
      <main>
        <Hero />
        <BookingForm onCreated={setReservation} />
        <ReservationLookup />
      </main>
      <Footer />
    </>
  )
}

export default App
