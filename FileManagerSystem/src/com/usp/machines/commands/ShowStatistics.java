package com.usp.machines.commands;

import com.usp.items.StatsManager;
import com.usp.items.StatisticRecord;

import java.util.*;
import java.util.stream.*;

/**
 * Comando responsável por exibir estatísticas de download com alta precisão.
 */
public class ShowStatistics implements Command {
    @Override
    public void execute(String[] args) {
        List<StatisticRecord> recs = StatsManager.getInstance().getRecords();
        if (recs.isEmpty()) {
            System.out.println("Nenhum registro de estatísticas disponível.");
            return;
        }

        // Cabeçalho com colunas e espaço para maior precisão
        System.out.printf(
            "%-10s | %-7s | %-12s | %-3s | %-15s | %-15s%n",
            "Tam. chunk", "N peers", "Tam. arquivo", "N", "Tempo [s]", "Desvio [s]"
        );

        // Agrupa por (chunkSize, numPeers, fileSize)
        Map<List<Long>, List<StatisticRecord>> grouped = recs.stream()
            .collect(Collectors.groupingBy(r -> Arrays.asList(
                (long) r.getChunkSize(),
                (long) r.getNumPeers(),
                r.getFileSize()
            )));

        // Para cada grupo, calcula média e desvio com 9 casas decimais
        grouped.forEach((key, group) -> {
            long chunk    = key.get(0);
            long peers    = key.get(1);
            long fileSize = key.get(2);
            int  n        = group.size();

            double mean = group.stream()
                .mapToDouble(StatisticRecord::getDuration)
                .average().orElse(0);
            double variance = group.stream()
                .mapToDouble(r -> Math.pow(r.getDuration() - mean, 2))
                .sum() / n;
            double std = Math.sqrt(variance);

            // Imprime linha de dados com precisão de microssegundos (9 casas)
            System.out.printf(
                "%-10d | %-7d | %-12d | %-3d | %-15.9f | %-15.9f%n",
                chunk, peers, fileSize, n, mean, std
            );
        });
    }
}

