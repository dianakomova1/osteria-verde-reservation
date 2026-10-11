type LogoProps = {
  dark?: boolean
}

function Logo({ dark = false }: LogoProps) {
  return (
    <a className={`logo${dark ? ' logo--dark' : ''}`} href="#top" aria-label="Osteria Verde — на главную">
      <span>OSTERIA <i className="logo__olive" aria-hidden="true" /> VERDE</span>
      <small>итальянский ресторан</small>
    </a>
  )
}

export default Logo
