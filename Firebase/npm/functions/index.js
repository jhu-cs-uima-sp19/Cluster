const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()
const os = require('os')
const path = require('path')
const spawn = require('child-process-promise').spawn
const fs = require('fs');


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


//either new file added or file deleted triggers change
exports.onFileChange = functions.storage.object().onFinalize(async event =>{ 
    console.log('Something happened to a file!')

    const fileBucket = object.bucket; // The Storage bucket that contains the file.
    const filePath = object.name; // File path in the bucket.
    const contentType = object.contentType; // File content type.
    const metageneration = object.metageneration; // Number of times metadata has been generated. New objects have a value of 1

  console.log('File change detected, function execution started')

  if (!contentType.startsWith('image/')) {
    return console.log('This is not an image.');
  }

  //get fileName
  const fileName = path.basename(filePath);

  if (fileName.startsWith('resized_')) {
    return console.log('Already resized.');
  
}

  // Download file from bucket.
const bucket = admin.storage().bucket(fileBucket);
const tempFilePath = path.join(os.tmpdir(), fileName);
const metadata = {
  contentType: contentType,
};
await bucket.file(filePath).download({destination: tempFilePath});
console.log('Image downloaded locally to', tempFilePath);
// Generate a thumbnail using ImageMagick.
await spawn('convert', [tempFilePath, '-thumbnail', '500x500>', tempFilePath]);
console.log('Thumbnail created at', tempFilePath);
// We add a 'thumb_' prefix to thumbnails file name. That's where we'll upload the thumbnail.
const thumbFileName = `resized_${fileName}`;
const thumbFilePath = path.join(path.dirname(filePath), thumbFileName);
// Uploading the thumbnail.
await bucket.upload(tempFilePath, {
  destination: thumbFilePath,
  metadata: metadata,
});
// Once the thumbnail has been uploaded delete the local file to free up disk space.
return fs.unlinkSync(tempFilePath);

})