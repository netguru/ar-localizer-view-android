package co.netguru.arlocalizer.arview

internal data class ARLabelProperties(
    var distance: Int,
    var positionX: Float,
    var positionY: Float,
    var alpha: Int,
    var id: Int = 0
) {
    override fun hashCode(): Int {
        return if (id != 0) id
        else super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (id != 0) other.hashCode() == hashCode()
        else super.equals(other)
    }
}
