# miniSASS App

The miniSASS Android app is an easy-to-use tool used for biomonitoring of river systems. It allows you to create sites and to add miniSASS assessments to the sites, the app makes it easy to create assessments and uses a new digital dichotomous key that guides you in selecting the correct macroinvertebrate group. The app also has a map view that displays all river assessments added by the community. The app makes use of a machine learning model, which will also predict each macroinvertebrate you add to an assessment. The machine learning classifications and score is also added to the assessment in addition to your selections and score, this allows the assessments to be verified faster than before. The app is connected to the miniSASS website, and your sites and assessments will be saved on the miniSASS website automatically when you create the sites and assessments, if you do not have an internet connection the sites and assessments will automatically be upload when you open the app and have an internet connection. There is also an about view where you can access miniSASS training videos and go through frequently ask questions about miniSASS.

# The app consists of the following views

## Login

The login screen allows you to login to the app, navigate to the registration page, and allows you to reset your password.

### How to login:

- Enter your email address and password of your miniSASS account into the appropriate fields and tap on the "LOGIN" button. If you do not have a miniSASS account yet, proceed to the how to register section.

### How to get to the registration view:

- From the login page tap on the "Create Account" link, this will take you to the Registration view.

### How to reset your password:

- From the login page tap on the "Forgot Password" link, this will open a reset password dialog. 
- Enter your email address into the email address field and tap on the "RESET" button.
- A link to reset your password will be sent to the provided email address if you have a registered miniSASS account.

## Registration

The registration page allows you to create an account that allows you to login to the app as well as the miniSASS website.

### How to register:

- Enter your personal and other details into the appropriate fields, when all the fields are populated tap on the "REGISTER" button.
- A link to finalise your registration will be emailed to the provided email address.
- Click on the link emailed to you, this will take you to the miniSASS website to complete your registration. 
- Once your registration is completed you can go back into the app to login.

## Home

The home view displays general information about miniSASS and the sponsors of the miniSASS app and website.

## Map

The map view displays all the river assessments for different sites made by the community in the form of color code crab markers. The river assessments are color coded and allow you to quickly see in what condition the river is in. You must have an internet connection to be able to fetch all the sites added by the community, if you do not have an internet connection only the sites you added through the app will be displayed.

### How to see the detailed view of a site:

- Tap on any site marker, this will open a detailed view of the site.

## Sites

The site view displays all the sites that you created. You can also create, update, and delete sites from this view and view the detailed view of each site.

### How to see the detailed view of a site:

- Tap on any site card, this will open a detailed view of the site.

### How to add a new site:

- Tap on the "+" fab, this will open the create site view.

### How to update a site:

- Tap on the "Edit" icon, this will open the site update view.
- Update any field you wish and tap on the "UPDATE SITE" button to update the site information.

### How to delete a site:

- Tap on the "Delete" icon, this will open a "Delete Site" dialog that will ask you if you are sure you want to delete the site.
- If you are sure you want to delete the site, tap on the "OK" button. 
- Otherwise, if you do not want to delete the site click on the "CANCEL" button.

Note: The site can only be deleted if you have an internet connection, this ensures that the website and app sites stay in sync. If you want to delete a site from the miniSASS app it will also have to be deleted on the miniSASS website.

## Create site

The create site view allows you to add a new site to the App and miniSASS website. The site will only be saved on the miniSASS website if you have an internet connection. If you do not have an internet connection when saving the site, the site will be saved on the miniSASS website when you open the app again and have an internet connection.

### How to create a new site:

- Enter the site details into the appropriate fields, when all the fields are populated tap on the "ADD SITE" button.
- Tap on the "TAKE SITE PHOTO" button to add images of the site, this will open the camera on the device.

Note: The site location field will be pre-populated by using the device location and cannot be changed. This is to ensure that the location is correct and not mistakenly entered incorrectly by the user. The Site Name, River Type, and Date fields are required to be populated before the site can be saved.

## Site Detail

The site detail view shows you a detailed description of the site with the following information: Site Name, River Name, Date, Description, and Site Images. It also shows all the assessments created for the site, each assessment displaying the miniSASS score  and condition of the user, the miniSASS Machine learning score and condition, Water Temperature, Dissolved Oxygen, Electrical Conductivity, Water Clarity, Ph, Notes, and the images of the macroinvertebrates observed with its user classification and the machine learning classification.

### How to add a miniSASS assessment to a site:

- From the Site Detail view, tap on the "ADD ASSESSMENT" button, this will open the Create Assessment view.

## Create Assessment

The create assessment view allows to create an assessment that will be added to a site, it allows you to take an image of an observed macroinvertebrate, crop the image if necessary, and to assign a group to the image by using a dropdown menu or a digital dichotomous key. When the assessment is saved, each image taken will be sent to the machine learning model to predict its miniSASS group, a miniSASS score is then calculated by using the user and model classifications and saved separately. By using both the user and machine learning score, the assessment can be verified faster than before.

### How to add water measurements:

- Tap on the "Add measurements" button, this will open the water measurements form.
- Enter the water measurements into the appropriate fields when all appropriate fields are populated tap on the "Hide" button.

### How to add observed macroinvertebrates to the assessment:

- Tap on the "CAPTURE" button, this will open the camera on the device.
- Take the photo of the observed macroinvertebrate.
- Crop the photo if necessary.
- Select the group you believe the captured macroinvertebrate belongs to from the "Select your identification" dropdown or by using the digital dichotomous key by tapping on  the "USE KEY" button.
- Tap on the "Add" button when you are finished selecting the identification, to add the macroinvertebrate to the assessment.
- Once added you will be taken back to the Create assessment view where you can add more macroinvertebrates to the assessment by following the step above.
- When you are finished adding the observed macroinvertebrates to the assessment, tap on the "SAVE ASSESSMENT" button.
- This will save the assessment on the miniSASS app and website if you have an internet connection. If you do not have an internet connection the assessment will be saved on the miniSASS website the next time you open the app and have an internet connection.

Note: When adding an assessment to a site, you will be asked if you want to upload the images taken as well? This is to ensure that you do not use mobile data you did not intend to use, because the total size of the images can be quite large. We do, however, urge users to upload the images as well. This will help grow the macroinvertebrate dataset used to train the machine learning model and increase the model’s accuracy in the future.

## About

The about view contains a list of frequently asked questions and answers, and training videos.

### How to open the frequently asked questions:

- Tap on any accordion heading for more information.

### How to view the training and education videos:

- Tap on the "TRAINING AN EDUCATION VIDEOS" Tab
- There are two offline videos integrated into the app, which do not require an internet connection to watch. The rest of the online videos require an internet connection to watch.
- Tap on any video thumbnail to watch the video.
