HOW TO USE:

  In your project’s build.gradle add the following line

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


And Add your dependencies in App Module Build.gradle

      dependencies {
	        implementation 'com.github.eduramza:RFormatterLibrary:Tag'
	    }
      
      
in your Activity or Fragment add this code for date Formatter:

     private fun setDateFormatter(){
            val type = DateFormatType.PT_BR
            val dateFormater = DateFormatter(WeakReference(edit_text), type)
            edit_text.addTextChangedListener(dateFormater)
        }
        
in your Activity or Fragment add this code for Phone Brazil Formatter:   

    private fun setPhoneFormatter(){
            val country =  PhoneNumberFormatType.PT_BR
            val phoneFormatter = PhoneNumberFormatter(WeakReference(edit_text), country)
            edit_text.addTextChangedListener(phoneFormatter)
        }
