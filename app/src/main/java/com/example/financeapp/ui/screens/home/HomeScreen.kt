package com.example.financeapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.financeapp.domain.model.DataSourceType
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val state = viewModel.state
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Diálogo de exclusão
    if (showDeleteDialog && selectedTransaction != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                Button(onClick = {
                    val deleted = selectedTransaction!!
                    viewModel.deleteTransaction(deleted)
                    showDeleteDialog = false
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Transação excluída",
                            actionLabel = "Desfazer",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.addTransaction(
                                deleted.description,
                                deleted.amount,
                                deleted.date,
                                deleted.type == TransactionType.INCOME
                            )
                        }
                    }
                }) { Text("Excluir") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            },
            title = { Text("Excluir transação") },
            text = { Text("Tem certeza que quer excluir esta transação?") }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("FinanceApp", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedTransaction = null
                showBottomSheet = true
            }) { Text("+", style = MaterialTheme.typography.headlineSmall) }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Indicador de fonte de dados ativa
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fonte: ${state.currentDataSource.name}",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Loading indicator quando dados estão sendo carregados
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                }
            }

            // Resumo financeiro
            SummarySection(state)
            Spacer(modifier = Modifier.height(16.dp))

            // Lista de transações ou estado vazio
            if (!state.isLoading && state.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nenhuma transação ainda", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Toque em + para adicionar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                TransactionList(
                    state = state,
                    onEdit = { transaction ->
                        selectedTransaction = transaction
                        showBottomSheet = true
                    },
                    onDelete = { transaction ->
                        selectedTransaction = transaction
                        showDeleteDialog = true
                    }
                )
            }
        }

        if (showBottomSheet) {
            TransactionBottomSheet(
                transaction = selectedTransaction,
                onDismiss = {
                    showBottomSheet = false
                    selectedTransaction = null
                },
                onSave = { description, value, date, isIncome ->
                    if (selectedTransaction == null) {
                        viewModel.addTransaction(description, value, date, isIncome)
                    } else {
                        selectedTransaction?.let { transaction ->
                            viewModel.updateTransaction(
                                transaction.copy(
                                    description = description,
                                    amount = value,
                                    date = date,
                                    type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
                                )
                            )
                        }
                    }
                    showBottomSheet = false
                    selectedTransaction = null
                }
            )
        }
    }
}
