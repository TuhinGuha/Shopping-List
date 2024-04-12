package com.example.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class ShoppingItemClass(val id:Int,
    var name:String,
    var quantity:Int,
    var isEditing:Boolean=false)

@Composable
fun shopping(){
    var itemList by rememberSaveable { mutableStateOf(listOf<ShoppingItemClass>()) }
    var itemName by rememberSaveable { mutableStateOf("") }
    var itemQuantity by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column (modifier = Modifier
        .fillMaxSize()
        .background(color = Color(29, 56, 73)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {showDialog=true},
            modifier = Modifier.padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(Color(0,198,176))) {
            Text(text = "Add Item", color = Color(255,247,214))
        }
        LazyColumn(){
            items(itemList){listItem->
                if(listItem.isEditing){
                    itemEditor(item = listItem,
                        onEditComplete = {editedName,editedQuantity->
                            itemList=itemList.map { it.copy(isEditing = false) }
                            val editingItem = itemList.find { it.id==listItem.id }
                            editingItem?.let {
                                it.name=editedName
                                it.quantity=editedQuantity
                            }
                        }
                    )
                }
                else{
                    itemRow(item = listItem,
                        onEditClick = {
                                      itemList=itemList.map { it.copy(isEditing = it.id==listItem.id) }
                        },
                        onDeleteClick = {itemList-=listItem})
                }
            }

        }
    }
    if(showDialog){
        AlertDialog(onDismissRequest = {
            itemName=""
            itemQuantity=""
            showDialog=false
        },
            containerColor = Color(29,56,73),
            confirmButton = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = {
                        if(itemName.isNotBlank() && itemQuantity.isNotBlank()){
                            val shoppingItemClassObject=ShoppingItemClass(itemList.size+1,itemName,itemQuantity.toIntOrNull()?:0)
                            itemList+=shoppingItemClassObject
                        }
                        itemName=""
                        itemQuantity=""
                        showDialog=false
                    },
                        colors = ButtonDefaults.buttonColors(Color(0,198,176))) {
                        Text(text = "Add", color = Color(255,247,214))
                    }
                    Button(onClick = {
                        itemName=""
                        itemQuantity=""
                        showDialog=false},
                        colors = ButtonDefaults.buttonColors(Color(0,198,176))) {
                        Text(text = "Cancel", color = Color(255,247,214))
                    }
                }
            },
            text = {
                Column {
                    OutlinedTextField(value = itemName,
                        onValueChange ={itemName=it},
                        label = {
                            Text(text = "Enter name", color = Color(255,247,214))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(Color(255,247,214)))
                    OutlinedTextField(value = itemQuantity,
                        onValueChange = {itemQuantity=it},
                        label = {
                            Text(text = "Enter quantity", color = Color(255,247,214))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(Color(255,247,214)),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        )
    }

}

@Composable
fun itemRow(item:ShoppingItemClass,
                    onEditClick:()->Unit,
                    onDeleteClick:()->Unit){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(2.dp, color = Color(255, 247, 214), shape = RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(modifier = Modifier.padding(4.dp).weight(7f),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Name: ${item.name}",
                modifier = Modifier.padding(start = 2.dp).weight(5f),
                color = Color(252,252,212))
            Text(text = "Quantity: ${item.quantity}", modifier = Modifier.weight(5f),
                color = Color(252,252,212))
        }
        Row(modifier = Modifier.padding(4.dp).weight(3f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {onEditClick()}) {
                Icon(imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit item",
                    tint = Color(255,247,214))
            }
            IconButton(onClick = {onDeleteClick()}) {
                Icon(imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = Color(255,247,214))
            }
        }
    }
}

@Composable
fun itemEditor(item: ShoppingItemClass,
               onEditComplete:(String,Int)->Unit){
    var editedName by rememberSaveable { mutableStateOf(item.name) }
    var editedQuantity by rememberSaveable { mutableStateOf(item.quantity.toString()) }
    var isEditing by rememberSaveable { mutableStateOf(item.isEditing) }



    Row(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(6f)) {
            OutlinedTextField(value = editedName,
                onValueChange = {editedName=it},
                label = {
                    Text(text = "Edit name", color = Color(255,247,214))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(Color(255,247,214)))
            OutlinedTextField(value = editedQuantity,
                onValueChange = {editedQuantity=it},
                label = {
                    Text(text = "Edit age", color = Color(255,247,214))
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(Color(255,247,214)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        Button(onClick = {
            onEditComplete(editedName,editedQuantity.toIntOrNull()?:0) },
            modifier = Modifier.weight(2f),
            colors = ButtonDefaults.buttonColors(Color(0,198,176)) ){
            Text(text = "Save", color = Color(255,247,214))
        }
    }
}