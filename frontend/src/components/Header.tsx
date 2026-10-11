import Logo from './Logo'

function Header() {
  return (
    <header className="header" id="top">
      <nav className="navigation" aria-label="Основная навигация">
        <Logo />
        <div className="navigation__links">
          <a href="#about">О ресторане</a>
          <a href="#lookup">Моя бронь</a>
          <a className="button button--small button--light" href="#booking">Забронировать</a>
        </div>
      </nav>
    </header>
  )
}

export default Header
