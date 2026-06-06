import { useContext } from "react"
import { LangContext } from "./LangContext"

export const useLang = () => {
  const context = useContext(LangContext)
  if (!context) throw new Error('useLang должен использоваться внутри LangProvider')
  return context
}
