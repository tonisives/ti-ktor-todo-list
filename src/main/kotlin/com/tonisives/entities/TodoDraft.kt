import kotlinx.serialization.Serializable

@Serializable
data class TodoDraft(val title: String, val done: Boolean)
