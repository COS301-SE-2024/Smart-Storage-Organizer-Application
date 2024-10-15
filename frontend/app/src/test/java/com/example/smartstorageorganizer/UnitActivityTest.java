//package com.example.smartstorageorganizer;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import android.os.Build;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//@RunWith(RobolectricTestRunner.class)
//@Config(sdk = Build.VERSION_CODES.P)
//public class UnitActivityTest {
//
//    UnitActivity Page;
//    @Mock
//    UnitActivity MockedPage = mock(UnitActivity.class);
//    @Before
//    public void setUp()  {
//        Page = Robolectric.buildActivity(UnitActivity.class)
//                .create()
//                .resume()
//                .get();
//    }
//
//    @Test
//    public void onCreate() {
//        assertNotNull(Page);
//    }
//
//    @Test
//    public void createUnit() {
//        //given
//        String unit="UnitName";
//        String capacity="1";
//        String contraints="contraints";
//        AtomicBoolean result = new AtomicBoolean(false);
//
//        //when
//        when(MockedPage.createUnit(unit, capacity, contraints)).thenReturn(CompletableFuture.completedFuture(true));
//
//        //then
//        MockedPage.createUnit(unit,capacity, contraints).thenAccept(isResult->{
//            if(isResult)
//                result.set(true);
//            else
//                result.set(false);
//
//        });
//        assertEquals(true, result.get());
//    }
//
//    @Test
//    public void getCategoriesId() {
//        //given
//        String name="Unit 1";
//        String id="";
//
//        assertEquals(id, Page.getCategoriesId(name));
//
//    }
//}