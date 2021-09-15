package com.github.sanctum.clans.construct.extra;

import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.clans.construct.impl.DefaultClan;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.OfflinePlayer;

public class MemberWrapper extends UniformedComponents<Clan.Associate> implements Serializable {

	private static final long serialVersionUID = -1769055045640489378L;
	private final DefaultClan c;

	public MemberWrapper(DefaultClan clan) {
		this.c = clan;
	}

	@Override
	public List<Clan.Associate> list() {
		FileManager c = ClansAPI.getData().getClanFile(this.c);
		return c.getConfig().getStringList("members").stream().map(i -> ClansAPI.getInstance().getAssociate(UUID.fromString(i)).orElse(null)).collect(Collectors.toList());
	}

	@Override
	public List<Clan.Associate> sort() {
		List<Clan.Associate> list = list();
		list.sort(Comparator.comparingDouble(Clan.Associate::getKD));
		return list();
	}

	@Override
	public List<Clan.Associate> sort(Comparator<? super Clan.Associate> comparable) {
		return sort();
	}

	@Override
	public Collection<Clan.Associate> collect() {
		return list();
	}

	@Override
	public Clan.Associate[] array() {
		return list().toArray(new Clan.Associate[0]);
	}

	@Override
	public <R> Stream<R> map(Function<? super Clan.Associate, ? extends R> mapper) {
		return list().stream().map(mapper);
	}

	@Override
	public Stream<Clan.Associate> filter(Predicate<? super Clan.Associate> predicate) {
		return list().stream().filter(predicate);
	}

	@Override
	public Clan.Associate getFirst() {
		return list().get(0);
	}

	@Override
	public Clan.Associate getLast() {
		return list().get(Math.max(list().size() - 1, 0));
	}

	@Override
	public Clan.Associate get(int index) {
		return list().get(index);
	}

	public UniformedComponents<OfflinePlayer> asPlayer() {
		return new MemberPlayerWrapper(this);
	}

}
