package com.example.driverproject.driver_slip;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Voucher extends AppCompatActivity {

    public static final int REQUEST_PERM_WRITE_STORAGE = 102;
    public static final int REQUEST_PERM_READ_STORAGE = 102;
    EditText name;
    //pdf
    private Button pdfSave;
    //pdf

    Toolbar toolbar;
    Button btn_get_sign, mClear, mGetSign, mCancel;

    File file;
    Dialog dialog;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;
    ImageView image;
    TextView textView4, textView5, textView6, textView7, textView8, textView9;

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        //pdf
        /*pdfSave = (Button) findViewById(R.id.submitSlip);

        pdfSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Voucher.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERM_WRITE_STORAGE);
                }
                else{
                    createPDF();
                    Intent i = new Intent(Voucher.this,EditProfileActivity.class);
                    startActivity(i);
                }
            }
        });

        pdfopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Voucher.this, "Pressed2", Toast.LENGTH_SHORT).show();
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Voucher.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERM_READ_STORAGE);
                }
                else{
                    openPDF();
                }
            }
        });*/

        //pdf

        Intent receive = getIntent();
        Bundle bundle = receive.getExtras();
        String vehicle_Type=bundle.getString("VehicleType");
        String vehicle_Number=bundle.getString("VehicleNumber");
        String date_journey=bundle.getString("dateofjourney");
        String start_kms=bundle.getString("start");
        String end_kms=bundle.getString("end");

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        int startkmsnum = Integer.parseInt(start_kms);

        int endkmsnum = Integer.parseInt(end_kms);

        int totaltravel = (endkmsnum - startkmsnum);
        image = (ImageView) findViewById(R.id.signatureImage);
        // Setting ToolBar as ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView4=(TextView)findViewById(R.id.textView4);

        textView5=(TextView)findViewById(R.id.textView5);

        textView6=(TextView)findViewById(R.id.textView6);

        textView7=(TextView)findViewById(R.id.textView7);

        textView8=(TextView)findViewById(R.id.textView8);

        textView9 = (TextView) findViewById(R.id.textView9);

        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.TollSlipImage);
        btn_get_sign = (Button) findViewById(R.id.signature);

        textView4.setText("Voucher Number:#");

        textView5.setText("Vehicle Type:"+vehicle_Type);

        textView6.setText("Vehicle Number:"+vehicle_Number);

        textView7.setText("Start KMS:"+start_kms);

        textView8.setText("End KMS:"+end_kms);

        textView9.setText("Total travel:" + Integer.toString(totaltravel) + "kms");

        dialog = new Dialog(Voucher.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCancelable(true);

        btn_get_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Function call for Digital Signature
                dialog_action();

            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Voucher.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERM_WRITE_STORAGE);
                } else {
                    createPDF();
                    Intent i = new Intent(Voucher.this, EditProfileActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference ref = storageReference.child("images/" + user.getUid().toString() + formattedDate + "/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Voucher.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Voucher.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void dialog_action() {

        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Bitmap b;
                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                b = mSignature.save(view);
                image.setImageBitmap(b);
                dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                // Calling the same class
                //recreate();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();
                // Calling the same class
                recreate();
            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public Bitmap save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                //FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);
                Toast.makeText(Voucher.this, "Passing Image", Toast.LENGTH_LONG).show();
                // Convert the output file to Image such as .png
                //bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                // mFileOutStream.flush();
                //mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
            return bitmap;

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    //pdf
    public void createPDF() {
        Toast.makeText(this, "In!", Toast.LENGTH_SHORT).show();

        Document doc = new Document();
        try {
            String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF";
            //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();

                Log.d("PDFCreator", "PDF Path: " + path);

                File file = new File(dir, "demo.pdf");
                FileOutputStream fout = new FileOutputStream(file);

                PdfWriter.getInstance(doc, fout);
                doc.open();

                //Toast.makeText(MainActivity.this, "Pressed", Toast.LENGTH_LONG).show();

                Paragraph p1 = new Paragraph("Voucher No:");
                Font paraFont = new Font();
                paraFont.setStyle(Font.BOLD);
                paraFont.setSize(16);
                p1.setAlignment(Paragraph.ALIGN_LEFT);
                p1.setFont(paraFont);
                doc.add(p1);

                Paragraph p2 = new Paragraph("Driver ID: ");
                Font paraFont2 = new Font(Font.FontFamily.TIMES_ROMAN, Font.BOLD);
                paraFont2.setColor(0, 0, 255);
                p2.setAlignment(Paragraph.ALIGN_LEFT);
                p2.setFont(paraFont2);
                doc.add(p2);

                int i = 10;
                Paragraph p3 = new Paragraph("Starting Reading(Km): " + i);
                Font paraFont3 = new Font();
                paraFont3.setStyle(Font.BOLD);
                paraFont3.setSize(16);
                p3.setSpacingBefore(10);
                p3.setAlignment(Paragraph.ALIGN_LEFT);
                p3.setFont(paraFont3);
                doc.add(p3);

                Paragraph p4 = new Paragraph("Ending Reading: ");
                Font paraFont4 = new Font();
                paraFont4.setStyle(Font.BOLD);
                paraFont4.setSize(16);
                p4.setSpacingBefore(10);
                p4.setAlignment(Paragraph.ALIGN_LEFT);
                p4.setFont(paraFont4);
                doc.add(p4);



                /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.driver1);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                Image myimg = Image.getInstance(stream.toByteArray());
                myimg.setDpi(100,100);
                myimg.setAlignment(Image.MIDDLE);

                doc.add(myimg);

                Phrase footertext = new Phrase("End");
                //HeaderFooter pdFFooter = new HeaderFooter(footertext,false);
                //doc.setFooter(pdFFooter);
*/
                Toast.makeText(this, "Created...", Toast.LENGTH_SHORT).show();
            }
        } catch (DocumentException de) {
            // de.printStackTrace();
            Log.e("PDFCreator", "Document Exception: " + de);
        } catch (IOException ioe) {
            //ioe.printStackTrace();
            Log.e("PDFCreator", "IO Exception: " + ioe);
        } finally {
            doc.close();
        }

    }

    public void openPDF() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDF";

        File file = new File(path, "demo.pdf");

        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);

    }
    //pdf
}
