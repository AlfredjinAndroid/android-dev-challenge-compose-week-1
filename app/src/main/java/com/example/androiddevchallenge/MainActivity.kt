/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

private val petList = mutableListOf<Pet>()

//private var adoptList = mutableStateOf(mutableListOf<Int>())
//private val adoptList by remember { mutableStateOf(listOf<Int>()) }

fun loadData() {
    val imgList = listOf(R.mipmap.pic_1, R.mipmap.pic_2, R.mipmap.pic_3, R.mipmap.pic_4)
    repeat(10) { index ->
        petList += Pet(
            id = index,
            name = "Dog$index",
            img = imgList[index % 4],
            desc = "Wow!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
        )
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        val controller = rememberNavController()
        val petViewModel: PetViewModel = viewModel()
        NavHost(
            navController = controller,
            startDestination = "list",
            builder = {
                composable(MyNav.List.router) {
                    ListPage(petList, controller)
                }
                composable(
                    MyNav.Details.Router.router,
                    arguments = listOf(navArgument("pet_id") { type = NavType.IntType })
                ) { entry ->
                    val petIndex = entry.arguments?.getInt("pet_id") ?: 0
                    PetDetailsPage(petIndex, controller, petViewModel)
                }
            })

    }
}
//
//@Preview("Light Theme", widthDp = 360, heightDp = 640)
//@Composable
//fun LightPreview() {
//    MyTheme {
//        MyApp()
//    }
//}
//
//@Preview("Dark Theme", widthDp = 360, heightDp = 640)
//@Composable
//fun DarkPreview() {
//    MyTheme(darkTheme = true) {
//        MyApp()
//    }
//}

@Composable
fun ListPage(petList: List<Pet>, navController: NavController) {
    LazyColumn(content = {
        items(petList) { pet ->
            ItemPet(pet, navController)
        }
    })
}

@Composable
fun ItemPet(pet: Pet, navController: NavController) {
    val pIndex = petList.indexOf(pet)
    val index = if (pIndex < 0) 0 else pIndex
    Card(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigateTo(MyNav.Details.Navigation(index))
            }
    ) {
        Row {
            Image(
                painter = painterResource(id = pet.img),
                contentDescription = pet.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(128.dp),
            )
            Box(
                modifier = Modifier
                    .height(128.dp)
                    .padding(16.dp)
            ) {
                ItemRight(pet)
            }
        }
    }
}

@Composable
fun ItemRight(pet: Pet) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = pet.name,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f, true)
        )
        Text(
            text = pet.desc,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f, true),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PetDetailsPage(id: Int, navController: NavController, petVm: PetViewModel = viewModel()) {
    val pet = petList.find { f -> f.id == id } ?: return
    Column {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                contentDescription = "Go Back",
            )
        }
        Image(
            painter = painterResource(id = pet.img), contentDescription = pet.name,
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            contentScale = ContentScale.Crop
        )

        val isAdopted = petVm.adoptedList.any { a -> a == pet.id }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                petVm.adp(pet = pet)
            }) {
                if (isAdopted) {
                    Text(text = "Adopted")
                } else {
                    Text(text = "Adopt")
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = pet.name)
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = pet.desc.repeat(30))
        }


    }
}

data class Pet(
    val id: Int,
    val name: String,
    val img: Int,
    val desc: String,
)

sealed class MyNav(val router: String) {
    object List : MyNav(router = "list")
    sealed class Details(router: String) : MyNav(router) {
        object Router : Details("details/{pet_id}")
        class Navigation(id: Int) : Details("details/$id")
    }
}

fun NavController.navigateTo(nav: MyNav, builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(nav.router, builder)
}

class PetViewModel : ViewModel() {
    var adoptedList by mutableStateOf(listOf<Int>())
        private set

    fun adp(pet: Pet) {
        val adopted = adoptedList.any { a -> a == pet.id }
        adoptedList = if (adopted) {
            adoptedList.toMutableList().also { it.remove(pet.id) }
        } else {
            adoptedList + listOf(pet.id)
        }
    }
}
