function Hero() {
  return (
    <section className="hero" id="about">
      <div className="hero__content">
        <div className="hero__copy">
          <p className="eyebrow">Итальянское гостеприимство</p>
          <h1>Стол для вашего идеального вечера</h1>
          <p className="hero__text">
            Выберите дату, время и количество гостей. Мы сразу проверим свободные столики и подтвердим бронь.
          </p>
          <a className="button" href="#booking">Выбрать столик</a>
          <ul className="hero__facts" aria-label="Информация о ресторане">
            <li><strong>12:00–23:00</strong><span>ежедневно</span></li>
            <li><strong>2 часа</strong><span>длительность брони</span></li>
            <li><strong>до 12 гостей</strong><span>за одним столом</span></li>
          </ul>
        </div>
        <figure className="hero__visual">
          <img src="/images/osteria-verde-interior.png" alt="Уютный зал ресторана Osteria Verde" />
          <figcaption>Тёплая атмосфера и любимая итальянская кухня</figcaption>
        </figure>
      </div>
    </section>
  )
}

export default Hero
