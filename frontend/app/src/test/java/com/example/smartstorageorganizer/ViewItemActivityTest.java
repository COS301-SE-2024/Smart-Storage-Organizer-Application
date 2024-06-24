package com.example.smartstorageorganizer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import android.os.Build;

import com.example.smartstorageorganizer.model.CategoryModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class ViewItemActivityTest {

    ViewItemActivity Page;
    @Mock
    ViewItemActivity MockedPage = mock(ViewItemActivity.class);
    @Before
    public void setUp()  {
        Page = Robolectric.buildActivity(ViewItemActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void onCreate() {
        assertNotNull(Page);
    }

    @Test
    public void  TestFindCategoryByName()
    {
        //given
        String categoryname="Electronics";

        //when
        CategoryModel result=Page.findCategoryByName(categoryname);
        String r;
        if(result==null)
        {
            r="";
        }
        else
        {
            r=result.toString();
        }
        //
        assertEquals("",r);

    }

}