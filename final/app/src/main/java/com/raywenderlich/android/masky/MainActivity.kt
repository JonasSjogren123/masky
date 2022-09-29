/*
 * Copyright (c) 2021 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.masky

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.raywenderlich.android.masky.databinding.ActivityMainBinding

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  private val maskDragMessage = "Mask Added"
  private val maskOn = "Bingo! Mask On"
  private val maskOff = "Mask off"

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    attachViewDragListener()
    attachViewDragListenerTest01()

    binding.maskDropArea.setOnDragListener(maskDragListener)



  }

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
  }

  /**
   * Method checks whether the mask is dropped on the face and
   * displays an appropriate Toast message
   *
   * @param dragEvent DragEvent
   */
  private fun checkIfMaskIsOnFace(dragEvent: DragEvent) {
    //x,y co-ordinates left-top point
    val faceXStart = binding.faceArea.x
    val faceYStart = binding.faceArea.y

    //x,y co-ordinates bottom-end point
    val faceXEnd = faceXStart + binding.faceArea.width
    val faceYEnd = faceYStart + binding.faceArea.height

    val toastMsg = if (dragEvent.x in faceXStart..faceXEnd && dragEvent.y in faceYStart..faceYEnd){
      maskOn
    } else {
      maskOff
    }

    Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show()

  }

  /**
   * Method enables drag feature on the draggable view
   */
  private fun attachViewDragListener() {

    binding.mask.setOnLongClickListener { view: View ->

      // Create a new ClipData.Item with custom text data
      val item = ClipData.Item(maskDragMessage)

      // Create a new ClipData using a predefined label, the plain text MIME type, and
      // the already-created item. This will create a new ClipDescription object within the
      // ClipData, and set its MIME type entry to "text/plain"
      val dataToDrag = ClipData(
          maskDragMessage,
          arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
          item
      )

      // Instantiates the drag shadow builder.
      val maskShadow = MaskDragShadowBuilder(view)

      // Starts the drag
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        //support pre-Nougat versions
        @Suppress("DEPRECATION")
        view.startDrag(dataToDrag, maskShadow, view, 0)
      } else {
        //supports Nougat and beyond
        view.startDragAndDrop(dataToDrag, maskShadow, view, 0)
      }

      view.visibility = View.INVISIBLE
      true
    }

  }

  private fun attachViewDragListenerTest01() {

    binding.test01.setOnLongClickListener { view: View ->

      // Create a new ClipData.Item with custom text data
      val item = ClipData.Item(maskDragMessage)

      // Create a new ClipData using a predefined label, the plain text MIME type, and
      // the already-created item. This will create a new ClipDescription object within the
      // ClipData, and set its MIME type entry to "text/plain"
      val dataToDrag = ClipData(
        maskDragMessage,
        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
        item
      )

      // Instantiates the drag shadow builder.
      val test01Shadow = Test01DragShadowBuilder(view)

      // Starts the drag
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        //support pre-Nougat versions
        @Suppress("DEPRECATION")
        view.startDrag(dataToDrag, test01Shadow, view, 0)
      } else {
        //supports Nougat and beyond
        view.startDragAndDrop(dataToDrag, test01Shadow, view, 0)
      }

      view.visibility = View.INVISIBLE
      true
    }

  }

}

/**
 * Drag shadow builder builds a shadow for the mask when drag is ongoing
 *
 * @param view View for which drag shadow has to be displayed
 */
/*private class MaskDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

  //set shadow to be the actual mask
  private val shadow = ResourcesCompat.getDrawable(view.context.resources, R.drawable.ic_mask, view.context.theme)

  // Defines a callback that sends the drag shadow dimensions and touch point back to the
  // system.
  override fun onProvideShadowMetrics(size: Point, touch: Point) {
    // Sets the width of the shadow to full width of the original View
    val width: Int = view.width

    // Sets the height of the shadow to full height of the original View
    val height: Int = view.height

    // The drag shadow is a Drawable. This sets its dimensions to be the same as the
    // Canvas that the system will provide. As a result, the drag shadow will fill the
    // Canvas.
    shadow?.setBounds(0, 0, width, height)

    // Sets the size parameter's width and height values. These get back to the system
    // through the size parameter.
    size.set(width, height)

    // Sets the touch point's position to be in the middle of the drag shadow
    touch.set(width / 2, height / 2)
  }

  // Defines a callback that draws the drag shadow in a Canvas that the system constructs
  // from the dimensions passed in onProvideShadowMetrics().
  override fun onDrawShadow(canvas: Canvas) {
    // Draws the Drawable in the Canvas passed in from the system.
    shadow?.draw(canvas)
  }
}*/

/*private class Test01DragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

  //set shadow to be the actual mask
  private val shadow =
    ResourcesCompat.getDrawable(view.context.resources, R.drawable.test_01, view.context.theme)

  // Defines a callback that sends the drag shadow dimensions and touch point back to the
  // system.
  override fun onProvideShadowMetrics(size: Point, touch: Point) {
    // Sets the width of the shadow to full width of the original View
    val width: Int = view.width

    // Sets the height of the shadow to full height of the original View
    val height: Int = view.height

    // The drag shadow is a Drawable. This sets its dimensions to be the same as the
    // Canvas that the system will provide. As a result, the drag shadow will fill the
    // Canvas.
    shadow?.setBounds(0, 0, width, height)

    // Sets the size parameter's width and height values. These get back to the system
    // through the size parameter.
    size.set(width, height)

    // Sets the touch point's position to be in the middle of the drag shadow
    touch.set(width / 2, height / 2)
  }

  // Defines a callback that draws the drag shadow in a Canvas that the system constructs
  // from the dimensions passed in onProvideShadowMetrics().
  override fun onDrawShadow(canvas: Canvas) {
    // Draws the Drawable in the Canvas passed in from the system.
    shadow?.draw(canvas)
  }
} */

