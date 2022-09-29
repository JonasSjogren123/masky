package com.raywenderlich.android.masky

import android.content.ClipDescription
import android.view.DragEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

/*class MaskDragListener {
    // Creates a mask drag event listener
    private val maskDragListener = View.OnDragListener { view, dragEvent ->

        val draggableItem = dragEvent.localState as View

        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                // dims the view when the mask has entered the drop area
                binding.maskDropArea.alpha = 0.3f
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                // reset opacity if the mask exits drop area without drop action
                binding.maskDropArea.alpha = 1.0f
                //when mask exits drop-area without dropping set mask visibility to VISIBLE
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                // reset opacity if the mask is dropped
                binding.maskDropArea.alpha = 1.0f

                //on drop event in the target drop area, read the data and
                // re-position the mask in it's new location
                if (dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    val draggedData = dragEvent.clipData.getItemAt(0).text
                    println("draggedData $draggedData")
                }

                //re-position the mask in the updated x, y co-ordinates
                // with mask center position pointing to new x,y co-ordinates
                draggableItem.x = dragEvent.x - (draggableItem.width / 2)
                draggableItem.y = dragEvent.y - (draggableItem.height / 2)

                //on drop event remove the mask from parent viewGroup
                val parent = draggableItem.parent as ConstraintLayout
                parent.removeView(draggableItem)

                //add the mask view to a new viewGroup where the mask was dropped
                val dropArea = view as ConstraintLayout
                dropArea.addView(draggableItem)

                checkIfMaskIsOnFace(dragEvent)
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            else -> {
                false
            }

        }
} */
