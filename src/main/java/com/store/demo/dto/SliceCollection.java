package com.store.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Collection wrapper to be used by HTTP response DTOs to wrap the collection and enhance the response with paging and sorting meta-data.
 *
 * @param <T> the type of the collection to wrap
 */
public class SliceCollection<T>
{
	@JsonProperty("value")
	private List<T> value;
	@JsonProperty("slice")
	private SliceMetaData slice;
	@JsonProperty("sort")
	private List<SortMetaData> sort;

	public SliceCollection()
	{
		this.value = new ArrayList();
	}

	public List<T> getValue()
	{
		return value;
	}

	public void setValue(final List<T> value)
	{
		this.value = value;
	}

	public SliceMetaData getSlice()
	{
		return slice;
	}

	public void setSlice(final SliceMetaData slice)
	{
		this.slice = slice;
	}

	public List<SortMetaData> getSort()
	{
		return sort;
	}

	public void setSort(final List<SortMetaData> sort)
	{
		this.sort = sort;
	}

	/**
	 * Create a new {@code SliceCollection} from a spring {@link Slice} element.
	 *
	 * @param slice the slice of results
	 * @param <T>   the type of the slice contents
	 * @return the slice collection wrapper
	 */
	public static <T> SliceCollection<T> of(final Slice<T> slice)
	{
		final SliceCollection slicedCollection = new SliceCollection();
		slicedCollection.setValue(slice.getContent());
		slicedCollection.setSlice(SliceMetaData.of(slice));

		Optional.ofNullable(slice.getSort())
				.filter(Sort::isSorted)
				.ifPresent(sort -> StreamSupport.stream(Spliterators.spliteratorUnknownSize(sort.iterator(), 0), false)
						.map(SortMetaData::of)
						.collect(Collectors.toList()));

		return slicedCollection;
	}

	/**
	 * Container for pagination meta-data.
	 */
	public static class SliceMetaData
	{
		private int number;
		private int size;
		private boolean hasNext;
		private boolean hasPrevious;

		public int getNumber()
		{
			return number;
		}

		public void setNumber(final int number)
		{
			this.number = number;
		}

		public int getSize()
		{
			return size;
		}

		public void setSize(final int size)
		{
			this.size = size;
		}

		public boolean isHasNext()
		{
			return hasNext;
		}

		public void setHasNext(final boolean hasNext)
		{
			this.hasNext = hasNext;
		}

		public boolean isHasPrevious()
		{
			return hasPrevious;
		}

		public void setHasPrevious(final boolean hasPrevious)
		{
			this.hasPrevious = hasPrevious;
		}

		public static SliceMetaData of(final Slice<?> slice)
		{
			final SliceMetaData sliceMetaData = new SliceMetaData();
			sliceMetaData.setNumber(slice.getNumber() + 1);
			sliceMetaData.setSize(slice.getSize());
			sliceMetaData.setHasNext(slice.hasNext());
			sliceMetaData.setHasPrevious(slice.hasPrevious());
			return sliceMetaData;
		}
	}

	/**
	 * Container for sorting meta-data.
	 */

	public static class SortMetaData
	{
		private String property;
		private String direction;

		public String getProperty()
		{
			return property;
		}

		public void setProperty(final String property)
		{
			this.property = property;
		}

		public String getDirection()
		{
			return direction;
		}

		public void setDirection(final String direction)
		{
			this.direction = direction;
		}

		public static SortMetaData of(final Sort.Order sortOrder)
		{
			final SortMetaData sortMetaData = new SortMetaData();
			sortMetaData.setProperty(sortOrder.getProperty());
			sortMetaData.setDirection(sortOrder.getDirection().name());
			return sortMetaData;
		}
	}
}