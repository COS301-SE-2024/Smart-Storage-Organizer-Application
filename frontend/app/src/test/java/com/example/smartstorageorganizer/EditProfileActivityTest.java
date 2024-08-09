package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class EditProfileActivityTest {

    EditProfileActivity editprofile1;
    @Mock
    EditProfileActivity editprofile = mock(EditProfileActivity.class);

    @Before
    public void setup() {
        editprofile1 = Robolectric.buildActivity(EditProfileActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(editprofile1);
    }


//    @Test
//    public void GetObjectUrlShouldReturnTrueForAnyKey()
//    {
//        //Given
//        String key="12345";
//        String result;
//
//        //When
//        when(editprofile1.GetObjectUrl(key)).thenReturn(key);
//
//        //Then
//        assertEquals(key, editprofile1.GetObjectUrl(key));
//    }

//    @Test
//    public void UploadProfilePictureShouldReturnTrueForValidFiles()
//    {
//        //Given
//        File image= new File("image");
//        AtomicBoolean result = new AtomicBoolean(false);
//
//        //When
//        when(editprofile1.UploadProfilePicture(image)).thenReturn(CompletableFuture.completedFuture(true));
//
//        //given
//        editprofile1.UploadProfilePicture(image).thenAccept(isResult->{
//            if(isResult)
//                result.set(true);
//            else
//                result.set(false);
//
//        });
//
//        assertEquals(true, result.get());
//
//    }

//    @Test
//    public void UploadProfilePictureShouldReturnTrueForInValidFiles()
//    {
//        //Given
//        File image= new File("invalidimage");
//        AtomicBoolean result = new AtomicBoolean(false);
//
//        //When
//        when(editprofile1.UploadProfilePicture(image)).thenReturn(CompletableFuture.completedFuture(false));
//
//        //given
//        editprofile1.UploadProfilePicture(image).thenAccept(isResult->{
//            if(isResult)
//                result.set(true);
//            else
//                result.set(false);
//
//        });
//
//        assertEquals(true, result.get());
//
//    }
//
}